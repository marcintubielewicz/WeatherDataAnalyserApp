package controller

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Get
import akka.stream.Materializer
import com.typesafe.config.ConfigFactory

import java.nio.file.{Files, Paths}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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

  // Set format for date and time timestamp
  val currentDateTime: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm"))

  /**
   * Fetches the weather data for the specified city and saves it as a JSON file.
   * @param cityName The name of the city for which to fetch the weather data.
   * @return A Future[Unit] that completes when the data is successfully fetched and saved, or if an error occurs.
   */
  def fetchAndSaveData(cityName: String): Future[Unit] = {
    // Fetch weather data for the specified city
    val responseFuture: Future[String] = fetchData(cityName)

    // Applies a transformation to the responseFuture using flatMap, which allows chaining of asynchronous operations.
    responseFuture.flatMap { json =>
      // Prepare the file path with the current date, time, and cityName
      val filePath = s"/Users/marcintubielewicz/Documents/programming/WeatherDataAnalyserApp/src/main/resources/$currentDateTime-$cityName.json"

      /**
       * Creates a new Future called writeFileFuture that represents the asynchronous operation of writing the fetched JSON data to a file
       * The Future is created using the Future constructor, which allows executing a block of code asynchronously.
       */
      val writeFileFuture = Future {
        Files.write(Paths.get(filePath), json.getBytes)
      }
      // Handles the completion of the writeFileFuture Future using the map method, which allows applying a transformation to the result of a Future.
      writeFileFuture.map { _ =>
        // File writing completed successfully
        println(s"Response received for $cityName. Data saved as JSON file: $filePath")
      }.recover {
        case exception =>
          // An error occurred while writing the data to the file
          println(s"An error occurred while writing data for $cityName: ${exception.getMessage}")
      }
    }.recover {
      case exception =>
        // An error occurred while fetching the weather data
        println(s"An error occurred while fetching data for $cityName: ${exception.getMessage}")
    }
  }
}