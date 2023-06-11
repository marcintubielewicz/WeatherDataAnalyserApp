package controllers

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Get
import akka.stream.Materializer
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContextExecutor, Future}

/**
 * The OpenWeatherMapApiClient object provides a client for fetching weather data
 * from the OpenWeatherMap API for a chosen city.
 */
object OpenWeatherMapApiClient {
  /**
   * Creates an implicit instance of ActorSystem, which is needed for creating actors and executing futures.
   * The ActorSystem is used to create an implicit instance of Materializer, which is needed for executing
   * Akka HTTP requests.
   */
  implicit val system: ActorSystem = ActorSystem()
  /**
   * Creates an implicit instance of Materializer, which is needed for materializing streams.
   * The Materializer is used to create an implicit instance of ExecutionContextExecutor,
   * which is needed for executing asynchronous operations.
   */
  implicit val materializer: Materializer = Materializer(system)
  /**
   * Creates an implicit execution context, which is needed for executing asynchronous operations.
   * The execution context is used to execute the Future returned by the fetchData method.
   */
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  // Loads the configuration from the "application.conf" file using the Typesafe Config library.
  private val config = ConfigFactory.load("application.conf")

  // Retrieves the API key value from the configuration file using the getString method.
  private val apiKey = config.getString("api_key.api_key")

  // Set main params for request
  val baseUrl = "https://api.openweathermap.org/data/2.5/weather"
  val units = "units=metric"
  val language = "lang=en"

  /**
   * The fetchData method takes a cityName parameter and returns a Future containing
   * a string representing the weather data for the chosen city.
   * The method uses Akka HTTP to send a GET request to the OpenWeatherMap API
   * with the specified cityName and API key, and returns the response body as a String.
   * @param cityName
   * @return
   */
  def fetchData(cityName: String): Future[String] = {
    val url = s"$baseUrl?q=$cityName&appid=$apiKey&$units&$language"
    println(url)
    val request = Get(url)
    Http().singleRequest(request)
      .flatMap(response => response.entity.toStrict(5.seconds).map(_.data.utf8String))
  }

  /**
   * The fetchAndPrintData method takes a cityName parameter and prints the
   * fetched weather data to the console using the println function.
   * The method internally calls the fetchData method to fetch the weather data.
   * @param cityName
   */
  def fetchAndPrintData(cityName: String): Unit = {
    val responseFuture = fetchData(cityName)
    responseFuture.map { json =>
      println(s"Response received for $cityName: $json")
    }
  }
}