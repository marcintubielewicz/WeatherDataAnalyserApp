package controller

import com.typesafe.config.{Config, ConfigFactory}
import io.circe.Json
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import io.circe.parser._
import java.io.File

import java.sql.{DriverManager, PreparedStatement}

object JsonToPostgres extends App {

  // Define the case classes representing the JSON data
  case class Clouds (
                      all: Int
                    )

  case class Coord (
                     lon: Double,
                     lat: Double
                   )

  case class Main (
                    temp: Double,
                    feels_like: Double,
                    temp_min: Double,
                    temp_max: Double,
                    pressure: Int,
                    humidity: Int
                  )

  case class WeatherData (
                             coord: Coord,
                             weather: Seq[Weather],
                             base: String,
                             main: Main,
                             visibility: Int,
                             wind: Wind,
                             clouds: Clouds,
                             dt: Int,
                             sys: Sys,
                             timezone: Int,
                             id: Int,
                             name: String,
                             cod: Int
                           )

    case class Sys(
                    `type`: Int,
                    id: Int,
                    country: String,
                    sunrise: Long,
                    sunset: Long
                  )

  case class Weather (
                       id: Int,
                       main: String,
                       description: String,
                       icon: String
                     )

  case class Wind (
                    speed: Double,
                    deg: Int
                  )

  // Load the configuration file and set the database connection parameters
  val config: Config = ConfigFactory.load("application.conf")

  val databaseUrl: String = config.getString("postgres.properties.url")
  val databaseName: String = config.getString("postgres.properties.databaseName")
  val username: String = config.getString("postgres.properties.user")
  val password: String = config.getString("postgres.properties.password")

  // Get the list of JSON files in the resources directory
  val directoryPath = "/Users/marcintubielewicz/Documents/programming/WeatherDataAnalyserApp/src/main/resources"
  val directory = new File(directoryPath)
  val jsonFiles = directory.listFiles.filter(_.getName.endsWith(".json"))

  // Iterate over the files and process them one by one
  jsonFiles.foreach { file =>
    val jsonString = scala.io.Source.fromFile(file).mkString
//    println("json string" + jsonString + "\n")

    // Parse the JSON string
    val json: Either[io.circe.Error, Json] = parse(jsonString)
//    println("json" + json + "\n")

    // Decode the JSON data into a case class
    val weatherData: Either[io.circe.Error, WeatherData] = json.flatMap(_.as[WeatherData])
//    println("weather data" + weatherData + "\n")

    // Insert the data into the PostgreSQL database
    weatherData match {
      case Right(data) =>
        insertDataToPostgres(data)
        println("Data inserted successfully")
      case Left(error) =>
        println(s"Failed to parse JSON: $error")
    }

    // Function to insert the data into PostgreSQL
    def insertDataToPostgres(data: WeatherData): Unit = {
      val conn = DriverManager.getConnection(databaseUrl, username, password)
      try {
        val pstmt: PreparedStatement = conn.prepareStatement(
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
        pstmt.setDouble(1, data.coord.lon)
        pstmt.setDouble(2, data.coord.lat)

        // Iterate over the list of Weather objects and insert each one into the database
        data.weather.zipWithIndex.foreach { case (weather, index) =>
          pstmt.setInt(3 + index, weather.id)
          pstmt.setString(4 + index, weather.main)
          pstmt.setString(5 + index, weather.description)
          pstmt.setString(6 + index, weather.icon)
        }

        pstmt.setString(7, data.base)
        pstmt.setDouble(8, data.main.temp)
        pstmt.setDouble(9, data.main.feels_like)
        pstmt.setDouble(10, data.main.temp_min)
        pstmt.setDouble(11, data.main.temp_max)
        pstmt.setInt(12, data.main.pressure)
        pstmt.setInt(13, data.main.humidity)
        pstmt.setInt(14, data.visibility)
        pstmt.setDouble(15, data.wind.speed)
        pstmt.setInt(16, data.wind.deg)
        pstmt.setInt(17, data.clouds.all)
        pstmt.setLong(18, data.dt)
        pstmt.setInt(19, data.sys.`type`)
        pstmt.setInt(20, data.sys.id)
        pstmt.setString(21, data.sys.country)
        pstmt.setLong(22, data.sys.sunrise)
        pstmt.setLong(23, data.sys.sunset)
        pstmt.setInt(24, data.timezone)
        pstmt.setInt(25, data.id)
        pstmt.setString(26, data.name)
        pstmt.setInt(27, data.cod)

        // Execute the insert statement
        pstmt.executeUpdate()
      } catch {
        case e: Exception => e.printStackTrace()
      } finally {
        conn.close()
      }
    }

  }
}
