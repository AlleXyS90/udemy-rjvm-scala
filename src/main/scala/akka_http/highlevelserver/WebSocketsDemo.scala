package akka_http.highlevelserver

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.util.CompactByteString
import scala.concurrent.duration._

object WebSocketsDemo extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer
  import scala.language.postfixOps

  // MEssage: TextMEssage vs BinaryMessage

  val textMessage = TextMessage(Source.single("hello via a text message"))
  val binaryMessage = BinaryMessage(Source.single(CompactByteString("hello via a binary message")))

  val html =
    """
      |<html>
      |<head>
      |    <script>
      |        // connect to server
      |        var exampleSocket = new WebSocket("ws://localhost:9001/greeter");
      |        console.log("starting websocket...");
      |
      |        // client receive messages from the server
      |        exampleSocket.onmessage = function (event) {
      |            var newChild = document.createElement("div");
      |            newChild.innerText = event.data;
      |            document.getElementById("1").appendChild(newChild);
      |        };
      |
      |        // send message to the server
      |        exampleSocket.onopen = function (event) {
      |            exampleSocket.send("socket seems to be open...");
      |        };
      |
      |        exampleSocket.send("socket says: hello, server");
      |    </script>
      |</head>
      |<body>
      |    Starting websocket...
      |    <div id="1">
      |    </div>
      |</body>
      |</html>
      |""".stripMargin

  def websocketFlow: Flow[Message, Message, Any] = Flow[Message].map {
    case tm: TextMessage =>
      TextMessage(Source.single("Server says back:") ++ tm.textStream ++ Source.single("!"))
    case bm: BinaryMessage =>
      bm.dataStream.runWith(Sink.ignore)
      TextMessage(Source.single("Server received a binary message..."))
  }

  case class SocialPost(owner: String, content: String)

  val socialFeed = Source(
    List(
      SocialPost("Martin", "Scala 3 has been announced"),
      SocialPost("Daniel", "A new rock the JVM course is open!"),
      SocialPost("Martin", "I killed Java.")
    )
  )

  // send one message at 2 seconds in the clientside
  val socialMessages = socialFeed
    .throttle(1, 2 seconds)
    .map(socialPost => TextMessage(s"${socialPost.owner} said: ${socialPost.content}"))

  val socialFlow: Flow[Message, Message, Any] = Flow.fromSinkAndSource(
    Sink.foreach[Message](println),
    socialMessages
  )

  val websocketRoute =
    (pathEndOrSingleSlash & get) {
      complete(HttpEntity(
        ContentTypes.`text/html(UTF-8)`,
        html
      ))
    } ~
      path("greeter") {
        handleWebSocketMessages(socialFlow)
      }

  Http().bindAndHandle(websocketRoute, "localhost", 9001)
}
