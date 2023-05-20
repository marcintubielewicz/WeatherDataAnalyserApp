package model

import akka.actor.ActorSystem
import controller.{JsonToPgSQL, OpenWeatherMapApiClient}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.io.Source

/**
 * Main class for weather data automation.
 * It fetches weather data from OpenWeatherMap API for a list of cities and saves it to a PostgreSQL database.
 * The process is scheduled to repeat every hour or until the application is terminated.
 */
object WeatherDataAutomation extends App {

  // Read city names from a text file
  val cityNames: Seq[String] = Source.fromFile("/Users/marcintubielewicz/Documents/programming/WeatherDataAnalyserApp/src/main/resources/city_names.txt").getLines().toList

  // Create an ActorSystem
  implicit val system: ActorSystem = ActorSystem()

  // Define the execution context
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  // Schedule the execution to repeat every hour
  system.scheduler.schedule(0.seconds, 1.hour) {
    for (city <- cityNames) {
      OpenWeatherMapApiClient.fetchAndSaveData(city)
      val json = new JsonToPgSQL()
      json.procesJsonFiles()
    }
  }

//  system.scheduler.scheduleAtFixedRate(0.seconds, 1.hour) { () =>
//    for (city <- cityNames) {
//      OpenWeatherMapApiClient.fetchAndSaveData(city)
//      val json = new JsonToPgSQL()
//      json.procesJsonFiles()
//    }
//  }
  // Keep the application running until termination
  try {
    // Wait for the scheduler to complete
    Await.ready(system.whenTerminated, Duration.Inf)
  } finally {
    // Terminate the actor system
    system.terminate()
  }
}
