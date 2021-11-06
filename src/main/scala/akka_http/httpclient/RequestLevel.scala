package akka_http.httpclient

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import PaymentSystemDomain.PaymentRequest
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import spray.json._

import scala.util.{Failure, Success}

object RequestLevel extends App with PaymentJsonProtocol {
  implicit val system = ActorSystem("RequestLevel")
  implicit val materializer = ActorMaterializer

  import system.dispatcher

  val responseFuture = Http().singleRequest(HttpRequest(uri = "http://www.google.com"))
  responseFuture.onComplete {
    case Success(response) =>
      // VERY IMPORTANT
      response.discardEntityBytes()
      println(s"The request was successful and returned : $response")
    case Failure(ex) =>
      println(s"The request failed with: $ex")
  }

  val creditCards = List(
    CreditCard("4242-4242-4242-4242", "424", "tx-test-account"),
    CreditCard("1234-1234-1234-1234", "123", "tx-daniels-account"),
    CreditCard("1234-1234-4321-4321", "321", "my-awesome-account")
  )

  val paymentRequests = creditCards.map(creditCard => PaymentRequest(creditCard, "rtjvm-store-account", 99))
  val serverHttpRequests = paymentRequests.map(paymentRequest =>
    HttpRequest(
      HttpMethods.POST,
      uri = Uri("http://localhost:9005/api/payments"),
      entity = HttpEntity(
        ContentTypes.`application/json`,
        paymentRequest.toJson.prettyPrint
      )
    )
  )

  Source(serverHttpRequests)
    .mapAsyncUnordered(10)(request => Http().singleRequest(request))
    .runForeach(println)
}
