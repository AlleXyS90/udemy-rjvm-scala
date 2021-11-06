package akka_http.highlevelserver

import java.io.File

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, Multipart}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Sink, Source}
import akka.util.ByteString

import scala.concurrent.Future
import scala.util.{Failure, Success}

object UploadingFiles extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer

  val filesRoute =
    (pathEndOrSingleSlash & get) {
      complete(
        HttpEntity(
          ContentTypes.`text/html(UTF-8)`,
          """
            |<form action="http://localhost:9002/upload" method="post" enctype="multipart/form-data">
            | <input type="file" name="myFile" />
            | <button type="submit">Upload</button>
            |</form>
            |""".stripMargin
        )
      )
    } ~
      (path("upload") & extractLog) { log =>
        // handle uploading files
        // multipart/form-data

        entity(as[Multipart.FormData]) { formData =>
          // form data contain the file
          // handle file payload
          val partsSource: Source[Multipart.FormData.BodyPart, Any] = formData.parts
          val filePartsSink: Sink[Multipart.FormData.BodyPart, Future[Done]] = Sink.foreach[Multipart.FormData.BodyPart] { bodyPart =>
            if (bodyPart.name == "myFile") {
              // create a file
              println(bodyPart.filename)
              val fileName = "akka/resources/download/" + bodyPart.filename.getOrElse("tempFile_" + System.currentTimeMillis())
              val file = new File(fileName)

              log.info(s"Writing to file: $fileName")

              val fileContentsSource: Source[ByteString, _] = bodyPart.entity.dataBytes
              val fileContentsSink: Sink[ByteString, _] = FileIO.toPath(file.toPath)

              // writing the data tot the file
              fileContentsSource.runWith(fileContentsSink)
            }
          }

          val writeOperationFuture = partsSource.runWith(filePartsSink)
          onComplete(writeOperationFuture) {
            case Success(_) => complete("File uploaded.")
            case Failure(ex) => complete(s"File failed to upload: $ex")
          }
        }
      }

  Http().bindAndHandle(filesRoute, "localhost", 9002)
}
