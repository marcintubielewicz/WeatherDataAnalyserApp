package model.DAO

import com.typesafe.config.ConfigFactory
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._


object Connection {

    // Load the configuration from the application.conf file
    val config = ConfigFactory.load()

    // Create the database using the configuration
    val db = Database.forConfig("application.conf/postgres")

}