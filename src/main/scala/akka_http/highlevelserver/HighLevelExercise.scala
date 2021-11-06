package akka_http.highlevelserver

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import HighLevelExercise.system
import PersonDb.{CreatePerson, FindAllPeoples, FindPerson}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.util.Timeout
import spray.json.DefaultJsonProtocol

import scala.concurrent.duration._
import scala.util.{Failure, Success}

object HighLevelExercise {
  implicit val system = ActorSystem("HighLevelExercise")
  implicit val materializer = ActorMaterializer

  /*
    -v GET /api/people: retrieve all the people you have registered
    -v GET /api/people/pin: retrieve the person with that PIN, return as JSON
    -v GET /api/people?pin=X (same)
    - POST /api/people with a JSON payload denoting a Person, add that person to your database
   */
}

case class Person(pin: Int, name: String)

trait PersonJsonProtocol extends DefaultJsonProtocol {
  implicit val peopleFormat = jsonFormat2(Person)
}

object PersonDb {

  case class CreatePerson(person: Person)

  case class PersonCreated(id: Int)

  case class FindPerson(id: Int)

  case object FindAllPeoples

}

class PersonDb extends Actor with ActorLogging {

  import PersonDb._

  var persons: Map[Int, Person] = Map()
  var currentPersonId: Int = 0

  override def receive: Receive = {
    case FindAllPeoples =>
      sender() ! persons.values.toList

    case FindPerson(id) =>
      sender() ! persons.get(id)

    case CreatePerson(person) =>
      persons = persons + (currentPersonId -> person)
      sender() ! PersonCreated(currentPersonId)
      currentPersonId += 1
  }
}


// server
object ExerciseRest extends App with PersonJsonProtocol {

  import akka.pattern.ask
  import spray.json._
  import system.dispatcher
  import scala.language.postfixOps

  val peopleDb = system.actorOf(Props[PersonDb], "HighLevelExercise")

  implicit val timeout = Timeout(2 seconds)

  var people = List(
    Person(1, "Alice"),
    Person(2, "Bob"),
    Person(3, "Charlie")
  )
  people.foreach { person =>
    peopleDb ! CreatePerson(person)
  }

  val peopleServerRoute =
    pathPrefix("api" / "people") {
      get {
        (path(IntNumber) | parameter('pin.as[Int])) { pin: Int =>
          complete(
            (peopleDb ? FindPerson(pin))
              .mapTo[Option[Person]]
              .map(_.toJson.prettyPrint)
              .map(toHttpEntity)
          )
        } ~
          pathEndOrSingleSlash {
            // list
            complete(
              (peopleDb ? FindAllPeoples)
                .mapTo[List[Person]]
                .map(_.toJson.prettyPrint)
                .map(toHttpEntity)
            )
          }
      } ~
        (post & pathEndOrSingleSlash & extractRequest & extractLog) { (request, log) =>
          val entity = request.entity
          val strictEntityFuture = entity.toStrict(2 seconds)
          val personFuture = strictEntityFuture.map(_.data.utf8String.parseJson.convertTo[Person])

          onComplete(personFuture) {
            case Success(person) =>
              peopleDb ! CreatePerson(person)
              people = people :+ person
              println("created")
              complete(StatusCodes.OK)
            case Failure(ex) =>
              failWith(ex)
          }
        }
    }

  def toHttpEntity(payload: String) = HttpEntity(ContentTypes.`application/json`, payload)

  Http().bindAndHandle(peopleServerRoute, "localhost", 9001)
}
