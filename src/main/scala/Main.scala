import controller.JsonToPostgres._
import controller.{JsonToPgSQL, OpenWeatherMapApiClient}

import scala.io.StdIn
object Main{
  def main(args: Array[String]): Unit = {
    println("Start to execute your request")

//    val jsonDataZielonka = OpenWeatherMapApiClient.fetchAndSaveData("Zielonka")
//    val jsonDataLondon = OpenWeatherMapApiClient.fetchAndSaveData("London")
//    val jsonDataBydgoszcz = OpenWeatherMapApiClient.fetchAndSaveData("Bydgoszcz")
//
//    val testResponseFromOpenWeatherAPI = OpenWeatherMapApiClient.fetchAndSaveData("Tokyo")
//    println(testResponseFromOpenWeatherAPI)

    // set up the connection to the database
//    val db_connection = new DatabaseConnector()
//    if (db_connection.getConnection().isValid(5)) {
//      println("Connection established")
//    } else {
//      println("Connection error, try again")
//    }

    // prompt the user to enter the city name
    // Prompt the user to enter the number of cities
    println("Enter the number of cities you want to fetch a data:")
    val numberOfCities = StdIn.readInt()
    // if numberOfCities < 1 then throw exception and back to previous step, else continue

    if (numberOfCities < 1) {
      throw new Exception("Number of cities must be greater than 0")
    } else {
      // Create an array to store the city names
      val cityNames = new Array[String](numberOfCities)

      // Prompt the user to enter the city names
      for (i <- 0 until numberOfCities) {
        println(s"Enter the name of city ${i + 1}:")
        cityNames(i) = StdIn.readLine()
      }

      // Print the city names
      println("You have choosen the following cities: ")
      for (city <- cityNames) {
        println(city)
      }
      // fetch the data for chosen cities from the OpenWeatherMap API and save it to the database with the current timestamp
      for (city <- cityNames) {
        OpenWeatherMapApiClient.fetchAndSaveData(city)
        val json = new JsonToPgSQL()
        json.procesJsonFiles()
      }
    }

    // close the connection to the database
//    db_connection.getConnection().close()
    println("Connection with PqSQL was closed")

  }
}
