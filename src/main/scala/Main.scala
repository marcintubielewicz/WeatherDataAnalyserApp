import akka.actor.ActorSystem
import akka.stream.Materializer
import controllers.{Averages, JsonToPgSQL}
import models.WeatherData

import scala.concurrent.ExecutionContext

object Main{
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
     **/
//    WeatherDataAutomation.main(Array())

    // Retrieve and print the weather data
    val jsonToPgSQL = new JsonToPgSQL
    val weatherData: Seq[WeatherData] = jsonToPgSQL.readDataFromPostgres()

    weatherData.foreach(println)

    val averages = new Averages
    val temperatureData = averages.readDataFromPostgres()
    averages.displayTemperatureData(temperatureData)

    println(averages.readDataFromPostgresCountry())




  }
}
