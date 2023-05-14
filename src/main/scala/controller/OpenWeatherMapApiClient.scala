/**
 * The `OpenWeatherMapApiClient` object provides a client for fetching weather data
 * from the OpenWeatherMap API for a chosen city.
 */
package controller

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Get
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

import java.nio.file.{Files, Paths}
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

object OpenWeatherMapApiClient {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  private val config = ConfigFactory.load("application.conf")
  private val apiKey = config.getString("api_key.api_key")

  val baseUrl = "https://api.openweathermap.org/data/2.5/weather"

  /**
   * The `fetchData` method takes a `cityName` parameter and returns a `Future` containing
   * a string representing the weather data for the chosen city.
   * The method uses Akka HTTP to send a GET request to the OpenWeatherMap API
   * with the specified `cityName` and API key, and returns the response body as a `String`.
   *
   * @param cityName
   * @return
   */

  def fetchData(cityName: String): Future[String] = {
    val url = s"$baseUrl?q=$cityName&appid=$apiKey"
    val request = Get(url)
    Http().singleRequest(request)
      .flatMap(response => response.entity.toStrict(5.seconds).map(_.data.utf8String))
  }

  /**
   * The `fetchAndPrintData` method takes a `cityName` parameter and prints the
   * fetched weather data to the console using the `println` function.
   * The method internally calls the `fetchData` method to fetch the weather data.
   *
   * @param cityName
   */

  def fetchAndPrintData(cityName: String): Unit = {
    val responseFuture = fetchData(cityName)
    responseFuture.map { json =>
      println(s"Response received for $cityName: $json")
    }
  }

  val currentDateTime = java.time.LocalDateTime.now()
  def fetchAndSaveData(cityName: String): Unit = {
    val responseFuture: Future[String] = fetchData(cityName)
    responseFuture.onComplete {
      case Success(json) =>
        val filePath = s"/Users/marcintubielewicz/Documents/programming/WeatherDataAnalyserApp/src/main/resources/$currentDateTime-$cityName.json"
        Files.write(Paths.get(filePath), json.getBytes)
        println(s"Response received for $cityName. Data saved as JSON file: $filePath")
      case Failure(exception) =>
        println(s"An error occurred while fetching data for $cityName: ${exception.getMessage}")
    }
  }
}