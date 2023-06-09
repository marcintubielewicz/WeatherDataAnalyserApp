package models

import com.typesafe.config.{Config, ConfigFactory}

import java.sql.{Connection, DriverManager, Statement}
import scala.concurrent.{ExecutionContext, Future}

/**
 * The WeatherDatabase class provides a database connection and functionality
 * to store weather data for different cities.
 *
 * @param databaseUrl   The URL of the PostgreSQL server.
 * @param databaseName  The name of the database to be created or used.
 * @param username      The username to authenticate with the database server.
 * @param password      The password to authenticate with the database server.
 * @param ec            The execution context for handling asynchronous operations.
 */
class WeatherDatabase()
                     (implicit ec: ExecutionContext) {

  //Loads the PostgreSQL driver.
  Class.forName("org.postgresql.Driver")


  // Load the configuration file
  val config: Config = ConfigFactory.load("application.conf")

  // Read the database connection properties from the config file
  val databaseUrl: String = config.getString("postgres.properties.url")
  val databaseName: String = config.getString("postgres.properties.databaseName")
  val username: String = config.getString("postgres.properties.user")
  val password: String = config.getString("postgres.properties.password")

  // Establish a database connection
  val connection: Connection = DriverManager.getConnection(databaseUrl, username, password)

  /**
   * Creates the specified database if it does not already exist.
   */
  val statement: Statement = connection.createStatement()
  statement.executeUpdate(s"CREATE DATABASE IF NOT EXISTS $databaseName;")
  statement.close()

  /**
   * Sets the connection's catalog to the newly created database.
   */
  connection.setCatalog(databaseName)

  /**
   * Creates the "weather_data" table if it does not already exist.
   * The table schema includes various columns representing different weather data attributes.
   */
  def createDatabaseAndTable(): Unit = {

    val createDatabaseStatement: Statement = connection.createStatement()
    createDatabaseStatement.executeUpdate(s"CREATE DATABASE IF NOT EXISTS $databaseName;")
    createDatabaseStatement.close()

    connection.setCatalog(databaseName)

    val createTableStatement: Statement = connection.createStatement()
    createTableStatement.executeUpdate(
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
    createTableStatement.close()

    connection.close()
  }
}

