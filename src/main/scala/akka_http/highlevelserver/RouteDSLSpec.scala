package akka_http.highlevelserver

import akka.http.javadsl.server.MethodRejection
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest._
import matchers.should._
import wordspec._
import spray.json.DefaultJsonProtocol

case class Book(id: Int, author: String, title: String)

trait BookJsonProtocol extends DefaultJsonProtocol {
  implicit val bookFormat = jsonFormat3(Book)
}

class RouteDSLSpec extends AnyWordSpec with Matchers with ScalatestRouteTest with BookJsonProtocol {

  import RouteDSLSpec._

  "A digital library backend" should {
    "return all the books in the library" in {
      // send an HTTP request through an endpoint that you want to test
      // inspect the response
      Get("/api/book") ~> libraryRoute ~> check {
        // assertions
        status shouldBe StatusCodes.OK
        entityAs[List[Book]] shouldBe books
      }
    }

    "return a book by hitting the query parameter endpoint" in {
      Get("/api/book?id=2") ~> libraryRoute ~> check {
        status shouldBe StatusCodes.OK
        responseAs[Option[Book]] shouldBe Some(Book(2, "JRR Tolien", "The lord of the Rings"))
      }
    }

    "insert a book into the database" in {
      val newBook = Book(5, "Steven Pressfield", "The War of Art")
      Post("/api/book", newBook) ~> libraryRoute ~> check {
        status shouldBe StatusCodes.OK
        assert(books.contains(newBook))
        books should contain(newBook) // same
      }
    }

    "not accept other methods than POST and GET" in {
      Delete("api/book") ~> libraryRoute ~> check {
//        rejections should not be empty // natural language style
//        rejections.should(not).be(empty) // same

        val methodRejections = rejections.collect {
          case rejection: MethodRejection => rejection
        }

        methodRejections.length shouldBe 2
      }
    }
  }
}

object RouteDSLSpec extends BookJsonProtocol with SprayJsonSupport {

  // code under test
  var books = List(
    Book(1, "Harper Lee", "To kill a Mockingbird"),
    Book(2, "JRR Tolien", "The lord of the Rings"),
    Book(3, "GRR Marting", "A song of Ice and Fire"),
    Book(4, "Tony Robbins", "Awaken the Giant Within")
  )

  val libraryRoute =
    pathPrefix("api" / "book") {
      (path(IntNumber) | parameter('id.as[Int])) { id =>
        complete(books.find(_.id == id))
      } ~
        pathEndOrSingleSlash {
          complete(books)
        }
    } ~ post {
      entity(as[Book]) { book =>
        books = books :+ book
        complete(StatusCodes.OK)
      } ~
        complete(StatusCodes.BadRequest)
    }
}
