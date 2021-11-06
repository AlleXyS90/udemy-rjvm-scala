package akka_http.lowlevelserver

import java.io.InputStream
import java.security.{KeyStore, SecureRandom}

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.{ConnectionContext, Http, HttpsConnectionContext}
import akka.stream.ActorMaterializer
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManagerFactory}

object HttpsContext {
  // step 1: key store
  val ks: KeyStore = KeyStore.getInstance("PKCS12")
   val keyStoreFile: InputStream = getClass.getClassLoader.getResourceAsStream("keysore.pkcs12")
  //val keyStoreFile = new FileInputStream(new File("C:/Users/tudor.alexandru/IdeaProjects/AkkaApp/AkkaAPI/app/akka/resources/keystore.pkcs12"))
  val password = "akka-https".toCharArray // fetch the password from a secure place
  ks.load(keyStoreFile, password)

  // step 2: initialize a key manager
  val keyManagerFactory = KeyManagerFactory.getInstance("SunX509") //PKI= public key infrastructure
  keyManagerFactory.init(ks, password)

  // step 3: initialize a trust manager
  val trustManagerFactory = TrustManagerFactory.getInstance("SunX509")
  trustManagerFactory.init(ks)

  // step 4: initialize an SSL context
  val sslContext: SSLContext = SSLContext.getInstance("TLS") // transport layer secured
  sslContext.init(keyManagerFactory.getKeyManagers, trustManagerFactory.getTrustManagers, new SecureRandom)

  // step 5: return the https connection context
  val httpsConnectionContext: HttpsConnectionContext = ConnectionContext.https(sslContext)
}

object LowLevelHttps extends App {
  implicit val system = ActorSystem("LowLevelHttps")
  implicit val materializer = ActorMaterializer

  val requestHandler: HttpRequest => HttpResponse = {
    case HttpRequest(HttpMethods.GET, _, _, _, _) =>
      HttpResponse(
        StatusCodes.OK,
        entity = HttpEntity(
          ContentTypes.`text/html(UTF-8)`,
          """
            |<html>
            | <body>
            |   Hello from Akka HTTP
            | </body>
            |</html>
            |""".stripMargin
        )
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

  val httpsBinding = Http().bindAndHandleSync(requestHandler, "localhost", 9005, HttpsContext.httpsConnectionContext)
}
