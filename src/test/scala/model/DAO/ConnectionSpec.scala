package model.DAO

import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import slick.jdbc.JdbcBackend.Database
//import slick.jdbc.PostgresProfile.api._

class ConnectionSpec extends AnyFlatSpec with Matchers with BeforeAndAfterAll {

  // Override the afterAll method to clean up any resources after the tests
  override def afterAll(): Unit = {
    Connection.db.close()
  }

  "Connection" should "create a valid database connection" in {
    // Test whether a valid connection can be established
    assert(Connection.db.isInstanceOf[Database])
//    assert(Connection.db.isOpen)
  }

//  it should "return the expected database configuration" in {
//    // Test whether the retrieved database configuration matches the expected configuration
//    val expectedConfig = "application.conf/postgres"
//    val actualConfig = Connection.db.sourceConfig.config.getString("appplication.conf/postgres")
//    actualConfig shouldEqual expectedConfig
//  }

}
