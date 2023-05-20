
import model.DAO.model.DAO.DatabaseConnector

import java.sql.Connection
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DatabaseConnectorSpec extends AnyFlatSpec with Matchers with BeforeAndAfterAll {

  // Create a test instance of the DatabaseConnector
  val connector = new DatabaseConnector()

  // Override the default database URL with a test-specific URL
  val testDatabaseUrl: String = "jdbc:postgresql://localhost:5432/test_database"

  override def beforeAll(): Unit = {
    // Set up any necessary test data or configuration before running the tests
    // For example, you can create a test database and insert some test data
  }

  override def afterAll(): Unit = {
    // Clean up any resources or test data after running the tests
    // For example, you can drop the test database
  }

  "DatabaseConnector" should "establish a database connection" in {
    // Call the getConnection() method
    val connection: Connection = connector.getConnection()

    // Assert that the connection is not null
    connection should not be null

  }

  it should "return a valid database connection" in {
    // Call the getConnection() method
    val connection: Connection = connector.getConnection()

    // Perform assertions to validate the connection
    connection.isValid(5) should be(true)

  }

}

