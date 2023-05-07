/**
 * The WeatherDatabase class provides a database connection and functionality
 * to store weather data for different cities.
 */
package model

import java.sql.{Connection, DriverManager}
import scala.concurrent.{ExecutionContext, Future}

class WeatherDatabase (databaseUrl: String, databaseName: String, username: String, password: String)
                     (implicit ec: ExecutionContext) {

//  val config = ConfigFactory.load("application.conf")
//
//  val databaseUrl = config.getString("db.url")
//  val databaseName = config.getString("db.name")
//  val username = config.getString("db.user")
//  val password = config.getString("db.password")

  // Load PostgreSQL driver
  Class.forName("org.postgresql.Driver")

  // Create a connection to the PostgreSQL server
  val connection: Connection = DriverManager.getConnection(databaseUrl, username, password)

  // Create the database if it does not exist
  val statement = connection.createStatement()
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
