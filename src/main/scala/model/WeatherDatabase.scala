/**
 * The WeatherDatabase class provides a database connection and functionality
 * to store weather data for different cities.
 */
package model

import com.typesafe.config.{Config, ConfigFactory}

import java.sql.{Connection, DriverManager, Statement}
import scala.concurrent.{ExecutionContext, Future}

class WeatherDatabase (databaseUrl: String,
                       databaseName: String,
                       username: String,
                       password: String)
                     (implicit ec: ExecutionContext) {
  // laad configuration from application.conf file
  val config: Config = ConfigFactory.load("application.conf")

  // Load PostgreSQL driver
  Class.forName("org.postgresql.Driver")

  // Create a connection to the PostgreSQL server
  val connection: Connection = DriverManager.getConnection(databaseUrl, username, password)

  // Create the database if it does not exist
  val statement: Statement = connection.createStatement()
  statement.executeUpdate(s"CREATE DATABASE IF NOT EXISTS $databaseName;")
  statement.close()

  // Use the new database
  connection.setCatalog(databaseName)

  // Create the table if it does not exist
  statement.executeUpdate(
    """CREATE TABLE IF NOT EXISTS weather_data (
      |    id SERIAL PRIMARY KEY,
      |    coord_lon FLOAT,
      |    coord_lat FLOAT,
      |    weather_id INT,
      |    weather_main VARCHAR(50),
      |    weather_description VARCHAR(255),
      |    weather_icon VARCHAR(10),
      |    base VARCHAR(50),
      |    main_temp FLOAT,
      |    main_feels_like FLOAT,
      |    main_temp_min FLOAT,
      |    main_temp_max FLOAT,
      |    main_pressure INT,
      |    main_humidity INT,
      |    visibility INT,
      |    wind_speed FLOAT,
      |    wind_deg INT,
      |    clouds_all INT,
      |    dt BIGINT,
      |    sys_type INT,
      |    sys_id INT,
      |    sys_country VARCHAR(3),
      |    sys_sunrise BIGINT,
      |    sys_sunset BIGINT,
      |    timezone INT,
      |    city_id INT,
      |    city_name VARCHAR(100),
      |    cod INT,
      |    created_at TIMESTAMP NOT NULL DEFAULT NOW()
      |    );""".stripMargin)
  statement.close()

  def saveWeather(city: String, json: String): Future[Unit] = Future {
    val preparedStatement = connection.prepareStatement("" +
      """
      INSERT INTO weather_data (
        coord_lon, coord_lat, weather_id, weather_main, weather_description, weather_icon, base,
        main_temp, main_feels_like, main_temp_min, main_temp_max, main_pressure, main_humidity,
        visibility, wind_speed, wind_deg, clouds_all, dt, sys_type, sys_id, sys_country, sys_sunrise,
        sys_sunset, timezone, city_id, city_name, cod, created_at
      ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
    """)
    preparedStatement.setString(1, city)
    preparedStatement.setString(2, json)
    preparedStatement.executeUpdate()
    preparedStatement.close()
  }
}
