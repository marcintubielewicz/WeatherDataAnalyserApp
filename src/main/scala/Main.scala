
import controller.JsonToPgSQL
import model.WeatherDataAutomation
import model.WeatherData
object Main{
  def main(args: Array[String]): Unit = {

  /**
   * The main entry point for the WeatherDataAutomation application.
   * This method is responsible for initiating the execution of the application.
   * It performs the necessary tasks and executes the code defined within the WeatherDataAutomation class.
   **/
    WeatherDataAutomation.main(Array())

    // Wait for a specified period of time to allow data fetching and saving
    Thread.sleep(10000)

    // Retrieve and print the weather data
    val jsonToPgSQL = new JsonToPgSQL
    val weatherData: Seq[WeatherData] = jsonToPgSQL.readDataFromPostgres()

    weatherData.foreach { weatherData =>
      println(weatherData)
    }
  }
}
