package akka_http.lowlevelserver

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow

object LowLevelIntro extends App {
  implicit val system = ActorSystem("LowLevelIntro")
  implicit val materialier = ActorMaterializer

  val streamsBasedRequestHandler: Flow[HttpRequest, HttpResponse, _] = Flow[HttpRequest].map {
    case HttpRequest(HttpMethods.GET, Uri.Path("/"), _, _, _) => // method, uri, http headers, content, protocol (http1.1/http2.0)
      HttpResponse(
        StatusCodes.OK,
        entity = HttpEntity(
          ContentTypes.`text/html(UTF-8)`,
          """
            |<html>
            | <body>
            |   Welcome!!
            | </body>
            |</html>
            |""".stripMargin
        )
      )
    case HttpRequest(HttpMethods.GET, Uri.Path("/about"), _, _, _) =>
      HttpResponse(
        StatusCodes.OK,
        entity = HttpEntity(
          ContentTypes.`text/html(UTF-8)`,
          """
            |<html>
            | <body>
            |   Page: <b>About</b>
            | </body>
            |</html>
            |""".stripMargin
        )
      )
    case HttpRequest(HttpMethods.GET, Uri.Path("/search"), _, _, _) =>
    HttpResponse(
      StatusCodes.Found,
      headers = List(Location("http://google.com"))
    )
    case request: HttpRequest =>
      request.discardEntityBytes()
      HttpResponse(
        StatusCodes.NotFound,
        entity = HttpEntity(
          ContentTypes.`text/html(UTF-8)`,
          """
            |<html>
            | <body>
            |   OOPS! The resouce can't be found
            | </body>
            |</html>
            |""".stripMargin
        )
      )
  }

  import system.dispatcher
  val bindingFuture = Http().bindAndHandle(streamsBasedRequestHandler, "localhost", 9003)
  //shut down server
  bindingFuture.flatMap(binding => binding.unbind())
    .onComplete(_ => system.terminate())
}
