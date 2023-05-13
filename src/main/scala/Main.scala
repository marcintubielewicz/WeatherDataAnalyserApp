import com.typesafe.config.{Config, ConfigFactory}
import model.WeatherDatabase

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main{
  def main(args: Array[String]): Unit = {
    println("Starting")

//    val jsonDataZielonka = OpenWeatherMapApiClient.fetchAndPrintData("Zielonka")
//    val jsonDataLondon = OpenWeatherMapApiClient.fetchAndPrintData("London")
//    val jsonDataBydgoszcz = OpenWeatherMapApiClient.fetchAndPrintData("Bydgoszcz")
//

    import scala.concurrent.ExecutionContext.Implicits.global

    // Assuming you have the necessary database credentials and dependencies
    val config: Config = ConfigFactory.load("application.conf")

      val databaseUrl: String = config.getString("db.url")
      val databaseName: String = config.getString("db.name")
      val username: String = config.getString("db.user")
      val password: String = config.getString("db.password")

    // Create an instance of WeatherDatabase
    val weatherDatabase = new WeatherDatabase(
      "database_url",
      "database_name",
      "username",
      "password")

    // Call the saveWeather method
    val city = "New York"
    val jsonData = "{...}" // Replace with the actual JSON data
    val saveResult = weatherDatabase.saveWeather(city, jsonData)

    // Handle the result asynchronously
    saveResult.foreach(_ => {
      // Save operation completed successfully
      println("Weather data saved successfully.")
    })

    // Wait for the operation to complete (if necessary)
    Await.result(saveResult, Duration.Inf) // Make sure to import scala.concurrent.Await and scala.concurrent.duration.Duration

  }
}
