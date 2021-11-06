package akka_streams.part4_techniques

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.util.Timeout

import java.util.Date
import scala.concurrent.Future

object IntegratingWithExternalServices extends App {
  implicit val system = ActorSystem("IntegratingWithExternalServices")
  implicit val materializer = ActorMaterializer
  // import system.dispatcher // not recommended in practice for mapAsync
  implicit val dispatcher = system.dispatchers.lookup("dedicated-dispatcher")
  // dedicated-dispatcher configured in application.conf

  def genericExternalService[A, B](element: A): Future[B] = ???

  // example: simplified PagerDuty

  case class PagerEvent(application: String,
                        description: String,
                        date: Date)

  val eventSource = Source(List(
    PagerEvent("AkkaInfra", "Infrastructure broke", new Date),
    PagerEvent("FastDataPipeline", "Illegal elements in the data pipeline", new Date),
    PagerEvent("AkkaInfra", "A service stopped responding", new Date),
    PagerEvent("SuperFrontend", "A button doesn't work", new Date)
  ))

  object PagerService {
    private val engineers = List("Alex", "John", "Daniel")
    private val emails = Map(
      "Alex" -> "alex@rockthejvm.com",
      "John" -> "john@rockthejvm.com",
      "Daniel" -> "daniel@rockthejvm.com"
    )

    def processEvent(pagerEvent: PagerEvent) = Future {
      val engineerIndex = pagerEvent.date.toInstant.getEpochSecond / (24 * 3600) % engineers.length
      val engineer = engineers(engineerIndex.toInt)
      val engineerEmail = emails(engineer)

      // page the engineer
      println(s"Sending engineer $engineerEmail a high priority notification: ${pagerEvent}")
      Thread.sleep(1000)

      // return the email that was paged
      engineerEmail
    }
  }

  val infraEvents = eventSource.filter(_.application == "AkkaInfra")
  val pagedEngineerEmails = infraEvents.mapAsync(parallelism = 4)(event => PagerService.processEvent(event))
  // mapAsync guarantees the relative order of elements
  val pagedEmailsSink = Sink.foreach[String](email => println(s"Successfully send notification to $email"))

//  pagedEngineerEmails.to(pagedEmailsSink).run

  class PagerActor extends Actor with ActorLogging {
    private val engineers = List("Alex", "John", "Daniel")
    private val emails = Map(
      "Alex" -> "alex@rockthejvm.com",
      "John" -> "john@rockthejvm.com",
      "Daniel" -> "daniel@rockthejvm.com"
    )

    private def processEvent(pagerEvent: PagerEvent) = {
      val engineerIndex = pagerEvent.date.toInstant.getEpochSecond / (24 * 3600) % engineers.length
      val engineer = engineers(engineerIndex.toInt)
      val engineerEmail = emails(engineer)

      // page the engineer
      log.info(s"Sending engineer $engineerEmail a high priority notification: ${pagerEvent}")
      Thread.sleep(1000)

      // return the email that was paged
      engineerEmail
    }

    override def receive: Receive = {
      case pagerEvent: PagerEvent =>
        sender() ! processEvent(pagerEvent)
    }
  }

  import akka.pattern.ask
  import scala.concurrent.duration._
  import scala.language.postfixOps
  implicit val timeout = Timeout(2 seconds)
  val pagerActor = system.actorOf(Props[PagerActor], "pagerActor")
  val alternativePagedEnginnerEmails = infraEvents.mapAsync(parallelism = 4)(event => (pagerActor ? event).mapTo[String])
  alternativePagedEnginnerEmails.to(pagedEmailsSink).run

  // do not confuse mapAsync with async (ASYNC boundary)
}
