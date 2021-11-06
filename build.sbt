name := "udemy-rjvm-scala"

version := "0.1"

scalaVersion := "2.13.7"

val akkaVersion = "2.6.16"
val akkaHttpVersion = "10.2.7"
val scalaTestVersion = "3.2.10"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream"          % akkaVersion,
  "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-akka_http.resources.json" % akkaHttpVersion,

  // test
  "com.typesafe.akka" %% "akka-testkit"         % akkaVersion,
  "com.typesafe.akka" %% "akka-stream-testkit"  % akkaVersion,
  "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion,
  "org.scalactic"     %% "scalactic"            % scalaTestVersion,
  "org.scalatest"     %% "scalatest"            % scalaTestVersion,

  // JWT
  "com.pauldijou"     %% "jwt-spray-akka_http.resources.json"       % "5.0.0"
)

