package views

import controllers.DatabaseConnection

import java.sql.ResultSet
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.Using

class Averages {

  /**
   * Reads the weather data from the PostgreSQL database and calculates the average temperature for each city.
   * @return A map of city names and their average temperatures.
   */
  def readDataFromPostgres(): Map[String, Double] = {
    Using.resource(new DatabaseConnection().getConnection) { connection =>
      val query: String = "SELECT city_name, main_temp FROM weather_data"
      Using.resource(connection.prepareStatement(query)) { statement =>
        Using.resource(statement.executeQuery()) { resultSet =>
          collectTemperatureData(resultSet)
        }
      }
    }
  }

  /**
   * Displays the average temperature for each city.
   * @param data A map of city names and their average temperatures.
   */
  def displayTemperatureData(data: Map[String, Double]): Unit = {
    data.foreach { case (city, temperature) =>
      val roundedTemperature = f"%%.2f".format(temperature)
      println(s"Area: $city, Average Temperature: $roundedTemperature")
    }
  }

  /**
   * Reads the weather data from the PostgreSQL database and calculates the average temperature for each sys_country.
   * @return A map of sys_country names and their average temperatures.
   */
  def readDataFromPostgresCountry(): Map[String, Double] = {
    Using.resource(new DatabaseConnection().getConnection) { connection =>
      val query: String = "SELECT sys_country, main_temp FROM weather_data"
      Using.resource(connection.prepareStatement(query)) { statement =>
        Using.resource(statement.executeQuery()) { resultSet =>
          collectTemperatureData(resultSet)
        }
      }
    }
  }

  /**
   * Collects the temperature data from the result set and calculates the average temperature for each group.
   * @param resultSet The result set containing the temperature data.
   * @return A map of group names (city or sys_country) and their average temperatures.
   */
  private def collectTemperatureData(resultSet: ResultSet): Map[String, Double] = {
    val temperatureData: mutable.Map[String, (Double, Int)] = mutable.Map.empty

    while (resultSet.next()) {
      val groupName = resultSet.getString(1)
      val temperature = resultSet.getDouble(2)

      temperatureData.get(groupName) match {
        case Some((sumTemp, count)) =>
          val newSumTemp = sumTemp + temperature
          val newCount = count + 1
          temperatureData.update(groupName, (newSumTemp, newCount))
        case None =>
          temperatureData.update(groupName, (temperature, 1))
      }
    }

    temperatureData.view.mapValues { case (sumTemp, count) => sumTemp / count }.toMap
  }
  /**
   * Invokes the database function averageMonthlyTempByCountry to retrieve the average monthly temperature by country
   * @return a list of tuples representing the result rows, where each tuple contains the following elements:
   *         - country: the name of the country (String)
   *         - averageTemperature: the average monthly temperature (Double)
   *         - month: the month (Integer)
   */
  def invokeAverageMonthlyTempByCountry(): List[(String, Double, Int)] = {
    Using.resource(new DatabaseConnection().getConnection) { connection =>
      val query: String = "SELECT * FROM averageMonthlyTempByCountry()"
      Using.resource(connection.prepareStatement(query)) { statement =>
        Using.resource(statement.executeQuery()) { resultSet =>
          val result = new ListBuffer[(String, Double, Int)]
          while (resultSet.next()) {
            val country = resultSet.getString(1)
            val averageTemperature = resultSet.getDouble(2)
            val month = resultSet.getInt(3)
            result += ((country, averageTemperature, month))
          }
          result.toList
        }
      }
    }
  }
  /**
   * Invokes the database function `averageMinMaxMonthlyTempByCountry` to retrieve the average, minimum, and maximum
   * monthly temperatures by country and city.
   * @return a list of tuples representing the result rows, where each tuple contains the following elements:
   *         - country: the name of the country (String)
   *         - city: the name of the city (String)
   *         - averageTemperature: the average monthly temperature (Double)
   *         - minTemperature: the minimum monthly temperature (Double)
   *         - maxTemperature: the maximum monthly temperature (Double)
   *         - month: the month (Integer)
   */
  def invokeAverageMinMaxMonthlyTempByCountry(): List[(String, String, Double, Double, Double, Int)] = {
    Using.resource(new DatabaseConnection().getConnection) { connection =>
      val query: String = "SELECT * FROM averageMinMaxMonthlyTempByCountry()"
      Using.resource(connection.prepareStatement(query)) { statement =>
        Using.resource(statement.executeQuery()) { resultSet =>
          val result = new ListBuffer[(String, String, Double, Double, Double, Int)]
          while (resultSet.next()) {
            val country = resultSet.getString("country")
            val city = resultSet.getString("city")
            val averageTemperature = resultSet.getDouble("average_temperature")
            val minTemperature = resultSet.getDouble("min_temp")
            val maxTemperature = resultSet.getDouble("max_temp")
            val month = resultSet.getInt("month")
            result += ((country, city, averageTemperature, minTemperature, maxTemperature, month))
          }
          result.toList
        }
      }
    }
  }
}
