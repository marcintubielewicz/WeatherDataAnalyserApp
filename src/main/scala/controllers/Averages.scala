package controllers

import java.sql.{PreparedStatement, ResultSet}

import scala.collection.mutable

class Averages {

  /**
   * Reads the weather data from the PostgresSQL database and calculates the average temperature for each city.
   * @return A map of city names and their average temperatures.
   */
  def readDataFromPostgres(): Map[String, Double] = {
    val conn = new DatabaseConnection().getConnection
    var statement: PreparedStatement = null
    var resultSet: ResultSet = null
    val temperatureData: mutable.Map[String, (Double, Int)] = mutable.Map.empty

    try {
      val query: String = "SELECT city_name, main_temp FROM weather_data"
      statement = conn.prepareStatement(query)
      resultSet = statement.executeQuery()

      while (resultSet.next()) {
        val cityName = resultSet.getString("city_name")
        val temperature = resultSet.getDouble("main_temp")

        temperatureData.get(cityName) match {
          case Some((sumTemp, count)) =>
            val newSumTemp = sumTemp + temperature
            val newCount = count + 1
            temperatureData.update(cityName, (newSumTemp, newCount))
          case None =>
            temperatureData.update(cityName, (temperature, 1))
        }
      }
    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      if (resultSet != null) resultSet.close()
      conn.close()
    }
    temperatureData.view.mapValues { case (sumTemp, count) => sumTemp / count }.toMap
  }
  /**
   * Displays the average temperature for each city.
   * @param data
   * A map of city names and their average temperatures.
   */
  def displayTemperatureData(data: Map[String, Double]): Unit = {
    data.foreach { case (city, temperature) =>
      val roundedTemperature = f"%%.2f".format(temperature)
      println(s"City: $city, Average Temperature: $roundedTemperature")
    }
  }
  //count average temperature for each sys_country and display it
  def readDataFromPostgresCountry(): Map[String, Double] = {
    val conn = new DatabaseConnection().getConnection
    var statement: PreparedStatement = null
    var resultSet: ResultSet = null
    val temperatureData: mutable.Map[String, (Double, Int)] = mutable.Map.empty

    try {
      val query: String = "SELECT sys_country, main_temp FROM weather_data"
      statement = conn.prepareStatement(query)
      resultSet = statement.executeQuery()

      while (resultSet.next()) {
        val countryName = resultSet.getString("sys_country")
        val temperature = resultSet.getDouble("main_temp")

        temperatureData.get(countryName) match {
          case Some((sumTemp, count)) =>
            val newSumTemp = sumTemp + temperature
            val newCount = count + 1
            temperatureData.update(countryName, (newSumTemp, newCount))
          case None =>
            temperatureData.update(countryName, (temperature, 1))
        }
      }
    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      if (resultSet != null) resultSet.close()
      conn.close()
    }
    temperatureData.view.mapValues { case (sumTemp, count) => sumTemp / count }.toMap
  }

}

