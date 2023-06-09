package controllers

import com.typesafe.config.{Config, ConfigFactory}

import java.sql.{Connection, DriverManager}


/**
 * The DatabaseConnection class is responsible for establishing the connection to the PostgresSQL database.
 */
class DatabaseConnection {
  // Load the configuration file and set the database connection parameters
  val config: Config = ConfigFactory.load("application.conf")
  val databaseUrl: String = config.getString("postgres.properties.url")
  val username: String = config.getString("postgres.properties.user")
  val password: String = config.getString("postgres.properties.password")

  private var connection: Connection = _

  /**
   * Creates a connection to the PostgresSQL database.
   * @return The connection object.
   */
  def getConnection: Connection = {
    if (connection == null || connection.isClosed) {
      connection = DriverManager.getConnection(databaseUrl, username, password)
    }
    connection
  }

  // Closes the database connection.
  def closeConnection(): Unit = {
    if (connection != null && !connection.isClosed) {
      connection.close()
    }
  }
}
