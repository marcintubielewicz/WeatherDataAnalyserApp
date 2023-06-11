package controllers

import akka.actor.ActorSystem
import models.WeatherData

import java.sql.PreparedStatement
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.Source
import scala.util.Using

/**
 * The WeatherDataAutomation object fetches weather data for a list of cities from the OpenWeatherMap API and saves it to a PostgreSQL database.
 * The data fetching and saving process is scheduled to repeat every hour.
 */
object WeatherDataAutomation extends App {

  // Create an implicit ActorSystem
  implicit val system: ActorSystem = ActorSystem("weather-data-automation")

  // Define the execution context, which is responsible for managing the execution of asynchronous tasks.
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  /**
   * Fetches weather data for the specified city from the OpenWeatherMap API and saves it to the database.
   * @param cityName The name of the city.
   * @return A Future representing the completion of the operation.
   */
  def fetchDataAndSaveToDatabase(cityName: String): Future[Unit] = {
    // Fetch weather data for the specified city
    val responseFuture: Future[String] = OpenWeatherMapApiClient.fetchData(cityName)

    responseFuture.flatMap { json =>
      // Process the fetched JSON data and save it to the database
      val weatherDataFuture: Future[Unit] = Future {
        // Decode the JSON data into a case class WeatherData
        val weatherData: Option[WeatherData] = decodeWeatherData(json)

        weatherData match {
          case Some(data) =>
            insertDataToPostgres(data)
            println(s"Data inserted successfully for $cityName")
          case None =>
            println(s"Failed to parse JSON for $cityName")
        }
      }

      weatherDataFuture.recover {
        case exception =>
          println(s"An error occurred while processing data for $cityName: ${exception.getMessage}")
      }
    }.recover {
      case exception =>
        println(s"An error occurred while fetching data for $cityName: ${exception.getMessage}")
    }
  }

  // Schedule the execution to repeat every hour
  system.scheduler.scheduleAtFixedRate(0.seconds, 1.hour) {
    () =>
      val cityNames: Seq[String] = Source.fromFile("/Users/marcintubielewicz/Documents/programming/WeatherDataAnalyserApp/src/main/resources/city_names.txt").getLines().toList
      for (cityName <- cityNames) {
        fetchDataAndSaveToDatabase(cityName)
      }
  }

  /**
   * Decodes the JSON data into a case class WeatherData.
   * @param json The JSON data as a string.
   * @return An Option containing the decoded WeatherData if successful, or None if unsuccessful.
   */
  private def decodeWeatherData(json: String): Option[WeatherData] = {
    import io.circe.generic.auto._
    import io.circe.parser._

    val decodedResult: Either[io.circe.Error, WeatherData] = decode[WeatherData](json)

    decodedResult match {
      case Right(weatherData) => Some(weatherData)
      case Left(_) => None
    }
  }

  /**
   * Inserts the WeatherData into the PostgreSQL database.
   * @param data The WeatherData object to be inserted.
   */
  def insertDataToPostgres(data: WeatherData): Unit = {
    // Database connection parameters
    Using(new DatabaseConnection().getConnection) { conn =>
      // Prepare the SQL statement
      val preparedStatement: PreparedStatement = conn.prepareStatement(
        """
          |INSERT INTO weather_data (
          |  coord_lon, coord_lat, weather_id, weather_main, weather_description, weather_icon, base,
          |  main_temp, main_feels_like, main_temp_min, main_temp_max, main_pressure, main_humidity,
          |  visibility, wind_speed, wind_deg, clouds_all, dt, sys_type, sys_id, sys_country, sys_sunrise,
          |  sys_sunset, timezone, city_id, city_name, cod
          |) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
          |""".stripMargin
      )

      // Set the parameter values
      preparedStatement.setDouble(1, data.coord.lon)
      preparedStatement.setDouble(2, data.coord.lat)

      // Iterate over the list of Weather objects and insert each one into the database
      data.weather.zipWithIndex.foreach { case (weather, index) =>
        preparedStatement.setInt(3 + index, weather.id)
        preparedStatement.setString(4 + index, weather.main)
        preparedStatement.setString(5 + index, weather.description)
        preparedStatement.setString(6 + index, weather.icon)
      }

      preparedStatement.setString(7, data.base)
      preparedStatement.setDouble(8, data.main.temp)
      preparedStatement.setDouble(9, data.main.feels_like)
      preparedStatement.setDouble(10, data.main.temp_min)
      preparedStatement.setDouble(11, data.main.temp_max)
      preparedStatement.setInt(12, data.main.pressure)
      preparedStatement.setInt(13, data.main.humidity)
      preparedStatement.setInt(14, data.visibility)
      preparedStatement.setDouble(15, data.wind.speed)
      preparedStatement.setInt(16, data.wind.deg)
      preparedStatement.setInt(17, data.clouds.all)
      preparedStatement.setLong(18, data.dt)
      preparedStatement.setInt(19, data.sys.`type`)
      preparedStatement.setInt(20, data.sys.id)
      preparedStatement.setString(21, data.sys.country)
      preparedStatement.setLong(22, data.sys.sunrise)
      preparedStatement.setLong(23, data.sys.sunset)
      preparedStatement.setInt(24, data.timezone)
      preparedStatement.setInt(25, data.id)
      preparedStatement.setString(26, data.name)
      preparedStatement.setInt(27, data.cod)

      // Execute the insert statement
      preparedStatement.executeUpdate()
    }.recover {
      case e: Exception => e.printStackTrace()
    }
  }
}
