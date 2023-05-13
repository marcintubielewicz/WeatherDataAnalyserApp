ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "WeatherDataAnalyserApp"
  )

/**
 * dependencies needed to build REST API app and interact with PostgresSQL database
 */
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.8.0",
  "com.typesafe.akka" %% "akka-http" % "10.5.0",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.5.0",
  "org.scalaj" %% "scalaj-http" % "2.4.2",
  "org.json4s" %% "json4s-jackson" % "4.0.6",
  "com.typesafe.akka" %% "akka-http-caching" % "10.5.0",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.5.0",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.8.0",
  "com.typesafe.akka" %% "akka-actor-typed" % "2.8.0",
  "com.typesafe.akka" %% "akka-persistence-typed" % "2.8.0",
  "com.typesafe.akka" %% "akka-cluster-typed" % "2.8.0",
  "org.scalatest" %% "scalatest" % "3.2.15" % Test,
  "com.typesafe.akka" %% "akka-http-testkit" % "10.5.0" est,
  "org.slf4j" % "slf4j-api" % "2.0.5",
  "org.slf4j" % "slf4j-simple" % "2.0.5",
  "org.postgresql" % "postgresql" % "42.5.4",
  "com.typesafe.slick" %% "slick" % "3.4.1",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.4.1",
  "com.github.tminglei" %% "slick-pg" % "0.21.1",
  "com.github.tminglei" %% "slick-pg_play-json" % "0.21.1"
)


