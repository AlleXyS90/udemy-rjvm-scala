package akka_http.highlevelserver

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.FromRequestUnmarshaller
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import spray.json._

import scala.concurrent.duration._

case class Player(nickname: String, characterClass: String, level: Int)

object GameAreaMap {

  case object GetAllPlayers

  case class GetPlayer(nickname: String)

  case class GetPlayersByClass(characterClass: String)

  case class AddPlayer(player: Player)

  case class RemovePlayer(player: Player)

  case object OperationSuccess

}

class GameAreaMap extends Actor with ActorLogging {

  import GameAreaMap._

  var players: Map[String, Player] = Map()

  override def receive: Receive = {
    case GetAllPlayers =>
      sender() ! players.values.toList

    case GetPlayer(nickname) =>
      sender() ! players.get(nickname)

    case GetPlayersByClass(characterClass) =>
      sender() ! players.values.toList.filter(_.characterClass == characterClass)

    case AddPlayer(player) =>
      players = players + (player.nickname -> player)
      sender() ! OperationSuccess

    case RemovePlayer(player) =>
      players = players - player.nickname
      sender() ! OperationSuccess
  }
}

trait PlayerJsonProtocol extends DefaultJsonProtocol {
  implicit val playerFormat = jsonFormat3(Player)
}

object MarshallingJSON extends App
  with PlayerJsonProtocol
  with SprayJsonSupport {

  import GameAreaMap._

  implicit val system = ActorSystem("MarshallingJSON")
  implicit val materializer = ActorMaterializer

  import system.dispatcher

  val rtjvmGameMap = system.actorOf(Props[GameAreaMap], "rockTheJVMGameAreaMap")
  val playersList = List(
    Player("martin", "Warrior", 70),
    Player("roland_007", "Elf", 45),
    Player("daniel_rock", "Wizard", 99)
  )
  playersList.foreach { player =>
    rtjvmGameMap ! AddPlayer(player)
  }

  /*
  - GET /api/player - returns all the players in the map
  - GET /api/player/nickname - returns the player with the given nickname
  - GET /api/player?nickname=X
  - GET /api/player/class/(charClass) - returns all the players with the given character class
  - POST /api/player with JSON payload, adds the player to the map
  - DELETE /api/player with JSON payload, removes the player from the map
   */

  import scala.language.postfixOps
  implicit val timeout = Timeout(2 seconds)
  var serverRoute =
    pathPrefix("api" / "player") {
      get {
        path("class" / Segment) { characterClass =>
          // get all the players with characterClass
          val playersByClassFuture = (rtjvmGameMap ? GetPlayersByClass(characterClass)).mapTo[List[Player]]
          complete(playersByClassFuture)
        } ~
          (path(Segment) | parameter('nickname)) { nickname =>
            // get the player with the nickname
            val playerFuture = (rtjvmGameMap ? GetPlayer(nickname)).mapTo[Option[Player]]
            complete(playerFuture)
          } ~
          pathEndOrSingleSlash {
            complete(
              (rtjvmGameMap ? GetAllPlayers)
                .mapTo[List[Player]]
                .map(_.toJson.prettyPrint)
            )
          }
      }
    } ~
      post {
        // add a player
        println("to add player")
        entity(implicitly[FromRequestUnmarshaller[Player]]) { player =>
          complete((rtjvmGameMap ? AddPlayer(player)).map(_ => StatusCodes.OK))
        }
      } ~
      delete {
        // delete a player
        entity(as[Player]) { player =>
          complete((rtjvmGameMap ? RemovePlayer(player)).map(_ => StatusCodes.OK))
        }
      }

  Http().bindAndHandle(serverRoute, "localhost", 9002)
}
