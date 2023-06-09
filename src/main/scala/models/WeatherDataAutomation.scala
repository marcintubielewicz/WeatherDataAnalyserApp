package models

import akka.actor.ActorSystem
import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.io.Source

import controllers.{JsonToPgSQL, OpenWeatherMapApiClient}

/**
 * Class for data automation, fetches weather data from OpenWeatherMap API for a list of cities and saves it to a PostgreSQL database.
 * The process is scheduled to repeat every hour or until the application is terminated.
 */
object WeatherDataAutomation extends App {

  // Read city names from a text file
  val cityNames: Seq[String] = Source.fromFile("/Users/marcintubielewicz/Documents/programming/WeatherDataAnalyserApp/src/main/resources/city_names.txt").getLines().toList

  // Create an ActorSystem
  val system: ActorSystem = ActorSystem("weather-data-automation")

  // Define the execution context, which is responsible for managing the execution of asynchronous tasks.
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  // Schedule the execution to repeat every hour
  system.scheduler.scheduleAtFixedRate(0.seconds, 1.hour) {
    () =>
      for (city <- cityNames) {
        OpenWeatherMapApiClient.fetchAndSaveData(city)
        val json = new JsonToPgSQL()
        json.procesJsonFiles()
      }
  }
}
