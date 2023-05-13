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

  val config: Config = ConfigFactory.load("application.conf")

//  val databaseUrl: String = config.getString("db.url")
//  val databaseName: String = config.getString("db.name")
//  val username: String = config.getString("db.user")
//  val password: String = config.getString("db.password")

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
    """CREATE TABLE IF NOT EXISTS weather (
      |  id SERIAL PRIMARY KEY,
      |  city VARCHAR(50) NOT NULL,
      |  json TEXT NOT NULL,
      |  created_at TIMESTAMP NOT NULL DEFAULT NOW()
      |);""".stripMargin)
  statement.close()

  def saveWeather(city: String, json: String): Future[Unit] = Future {
    val preparedStatement = connection.prepareStatement("INSERT INTO weatherappdata (city, json) VALUES (?, ?);")
    preparedStatement.setString(1, city)
    preparedStatement.setString(2, json)
    preparedStatement.executeUpdate()
    preparedStatement.close()
  }
}
