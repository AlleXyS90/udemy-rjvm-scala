package akka_http.highlevelserver

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka_http.lowlevelserver.HttpsContext

object HighLevelIntro extends App {
  implicit val system = ActorSystem("HighLevelIntro")
  implicit val materializer = ActorMaterializer
  import system.dispatcher

  // directives
  import akka.http.scaladsl.server.Directives._
  val simpleRoute: Route =
    path("home") { // DIRECTIVE
      complete(StatusCodes.OK) // DIRECTIVE
    }

  val pathGetRoute: Route =
    path("home") {
      get {
        complete(StatusCodes.OK)
      }
    }

  // chaining directives with
  val chainedRoute: Route =
    path("myEndpoint") {
      get {
        complete(StatusCodes.OK)
      }
      post {
        complete(StatusCodes.Forbidden)
      }
    } ~
  path("home") {
    complete(
      HttpEntity(
        ContentTypes.`text/html(UTF-8)`,
        """
          |Hello from the high level Akka HTTP!
          |""".stripMargin
      )
    )
  }

  Http().bindAndHandle(chainedRoute, "localhost", 9009)
}
