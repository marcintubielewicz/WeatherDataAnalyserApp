package model.DAO

import com.typesafe.config.{Config, ConfigFactory}
import java.sql.{Connection, DriverManager}

/**
 * The DatabaseConnector class is responsible for establishing a connection to the PostgreSQL database
 * based on the configuration specified in the application.conf file.
 * @param config The configuration object that provides access to the database connection properties.
 */
class DatabaseConnector {

  // Load the configuration file
  val config: Config = ConfigFactory.load("application.conf")

  // Read the database connection properties from the config file
  val databaseUrl: String = config.getString("postgres.properties.url")
  val databaseName: String = config.getString("postgres.properties.databaseName")
  val username: String = config.getString("postgres.properties.user")
  val password: String = config.getString("postgres.properties.password")

  // Establish a database connection
  def getConnection: Connection = {
    DriverManager.getConnection(databaseUrl, username, password)
  }
}

