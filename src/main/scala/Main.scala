import akka.actor.ActorSystem
import akka.stream.Materializer
import controllers.WeatherDataAutomation
import views.{Averages, WeatherDataPrinter}

import scala.concurrent.ExecutionContext

object Main {
  def main(args: Array[String]): Unit = {

    // Create an implicit ActorSystem
    implicit val system: ActorSystem = ActorSystem("weather-data-automation")

    // Create an implicit ExecutionContext
    implicit val executionContext: ExecutionContext = system.dispatcher

    // Create an implicit ActorMaterializer
    implicit val materializer: Materializer = Materializer(system)

    /**
     * The main entry point for the WeatherDataAutomation application.
     * This method is responsible for initiating the execution of the application.
     * It performs the necessary tasks and executes the code defined within the WeatherDataAutomation class.
     */
    WeatherDataAutomation.main(Array())

    // Create an instance of the Averages class
    val averages = new Averages

    // Read temperature data from Postgres for each city and display it
    val temperatureDataByCity = averages.readDataFromPostgres()
    averages.displayTemperatureData(temperatureDataByCity)

    // Read temperature data from Postgres for each country and display it
    val temperatureDataByCountry = averages.readDataFromPostgresCountry()
    averages.displayTemperatureData(temperatureDataByCountry)

    // Invoke the averageMonthlyTempByCountry procedure and fetch the results
    val results = averages.invokeAverageMonthlyTempByCountry()

    // Invoke the averageMinMaxMonthlyTempByCountry procedure and fetch the results
    val results2 = averages.invokeAverageMinMaxMonthlyTempByCountry()

    // Display the results of PgSQL function averageMonthlyTempByCountry
    results.foreach { case (country, averageTemperature, month) =>
      println(s"Country: $country, Average Temperature: $averageTemperature, Month: $month")
    }

    // Display the results of PgSQL function averageMinMaxMonthlyTempByCountry
    results2.foreach { case (country, city, averageTemperature, minTemperature, maxTemperature, month) =>
      println(s"Country: $country, City: $city, Average Temperature: $averageTemperature, Min_temp: $minTemperature, Max_temp: $maxTemperature, Month: $month")
    }

    // Invoke the main method of the WeatherDataPrinter class
    WeatherDataPrinter.main(Array())
  }
}
