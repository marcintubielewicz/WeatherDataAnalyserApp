package controllers

import io.circe.Json
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import io.circe.parser._
import models._
import java.io.File
import java.sql.{PreparedStatement, ResultSet}
import scala.util.Using

/**
 * The JsonToPgSQL class is responsible for processing JSON files
 * and inserting the data into a PostgresSQL database.
 */
class JsonToPgSQL {

  // Processes JSON files by parsing, decoding, inserting data into a PostgresSQL database, and deleting the processed files.
  def procesJsonFiles(): Unit = {

    // Get the list of JSON files from the resources directory
    val directoryPath = "/Users/marcintubielewicz/Documents/programming/WeatherDataAnalyserApp/src/main/resources"
    val directory = new File(directoryPath)
    val jsonFiles = directory.listFiles.filter(_.getName.endsWith(".json"))



    // ...

    jsonFiles.foreach { file =>
      Using(scala.io.Source.fromFile(file)) { source =>
        val jsonString = source.mkString

        // Parse the JSON string
        val json: Either[io.circe.Error, Json] = parse(jsonString)

        // Decode the JSON data into a case class WeatherData
        val weatherData: Either[io.circe.Error, WeatherData] = json.flatMap(_.as[WeatherData])

        // Insert the data into the PostgresSQL database
        weatherData match {
          case Right(data) =>
            insertDataToPostgres(data)
            println("Data inserted successfully")
          case Left(error) =>
            println(s"Failed to parse JSON: $error")
        }
      }

      // Delete the imported JSON file
      file.delete()
    }
  }

  /**
   * Inserts the weather data into the PostgresSQL database.
   * @param data
   *   The weather data to be inserted.
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

  /**
   * Reads weather data from the PostgresSQL database.
   * @return
   *   A sequence of weather data retrieved from the database.
   */
  def readDataFromPostgres(): Seq[WeatherData] = {
    // Database connection parameters
    Using(new DatabaseConnection().getConnection) { conn =>
      var statement: PreparedStatement = null
      var resultSet: ResultSet = null
      var data: Seq[WeatherData] = Seq.empty

      try {
        val query: String = "SELECT * FROM weather_data"
        statement = conn.prepareStatement(query)
        resultSet = statement.executeQuery()

        while (resultSet.next()) {
          val coord = Coord(resultSet.getDouble("coord_lon"), resultSet.getDouble("coord_lat"))

          val weatherList = (3 to 6).map { index =>
            Weather(
              resultSet.getInt(s"weather_id"),
              resultSet.getString(s"weather_main"),
              resultSet.getString(s"weather_description"),
              resultSet.getString(s"weather_icon")
            )
          }

          val main = Main(
            resultSet.getDouble("main_temp"),
            resultSet.getDouble("main_feels_like"),
            resultSet.getDouble("main_temp_min"),
            resultSet.getDouble("main_temp_max"),
            resultSet.getInt("main_pressure"),
            resultSet.getInt("main_humidity")
          )

          val wind = Wind(resultSet.getDouble("wind_speed"), resultSet.getInt("wind_deg"))

          val clouds = Clouds(resultSet.getInt("clouds_all"))

          val sys = Sys(
            resultSet.getInt("sys_type"),
            resultSet.getInt("sys_id"),
            resultSet.getString("sys_country"),
            resultSet.getLong("sys_sunrise"),
            resultSet.getLong("sys_sunset")
          )

          val weatherData = WeatherData(
            coord,
            weatherList,
            resultSet.getString("base"),
            main,
            resultSet.getInt("visibility"),
            wind,
            clouds,
            resultSet.getInt("dt"),
            sys,
            resultSet.getInt("timezone"),
            resultSet.getInt("city_id"),
            resultSet.getString("city_name"),
            resultSet.getInt("cod")
          )

          data = data :+ weatherData
        }
      } catch {
        case e: Exception => e.printStackTrace()
      } finally {
        if (resultSet != null) resultSet.close()
        if (statement != null) statement.close()
      }
      data
    }.getOrElse(Seq.empty)
  }
}
