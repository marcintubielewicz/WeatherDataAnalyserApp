ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "WeatherDataAnalyserApp"
  )

libraryDependencies ++= Seq(
  "org.json4s" %% "json4s-jackson" % "4.0.6",
  "org.scalaj" %% "scalaj-http" % "2.4.2"
)