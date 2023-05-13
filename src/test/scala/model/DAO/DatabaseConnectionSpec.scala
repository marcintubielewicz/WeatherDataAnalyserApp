package model.DAO

import java.sql.{Connection, DriverManager, ResultSet}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DatabaseConnectionSpec extends AnyFlatSpec with Matchers {
  "DatabaseConnection" should "establish a connection to the database and retrieve data" in {
    val url = "jdbc:postgresql://localhost:5432/weatherappdata"
    val username = "marcintubielewicz"
    val password = ""

    var connection: Connection = null

    try {
      // Load the PostgreSQL JDBC driver
      Class.forName("org.postgresql.Driver")

      // Establish the database connection
      connection = DriverManager.getConnection(url, username, password)

      // Create a statement
      val statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)

      // Execute a query to select all rows from the weather_data table
      val resultSet = statement.executeQuery("SELECT * FROM weather_data")

      // Verify that at least one row is returned
      resultSet.next() shouldEqual true

      // Verify that the "quote" column is not null
      resultSet.getString("id") should not be null
    } catch {
      case e: Exception => fail("An exception occurred: " + e.getMessage)
    } finally {
      // Close the database connection
      if (connection != null) {
        try {
          connection.close()
        } catch {
          case e: Exception => fail("Failed to close the database connection: " + e.getMessage)
        }
      }
    }
  }
}

