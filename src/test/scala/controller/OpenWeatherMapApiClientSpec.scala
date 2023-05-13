/**
*The OpenWeatherMapApiClientSpec class is a ScalaTest for testing the OpenWeatherMapApiClient class.
*It extends AsyncFlatSpec and uses Matchers for defining test cases.
 */
package controller

import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import java.nio.file.{Files, Paths}
import scala.language.postfixOps

class OpenWeatherMapApiClientSpec extends AsyncFlatSpec with Matchers {

  it should "fetch weather data for a city" in {
    val cityName = "Bydgoszcz"
    val responseFuture = OpenWeatherMapApiClient.fetchData(cityName)

    responseFuture.map { response =>
      response should include(cityName)
    }
  }

  it should "print weather data for a city" in {
    val cityName = "Zielonka"
    val response = OpenWeatherMapApiClient.fetchAndPrintData(cityName)
    Thread.sleep(2000)
    succeed
  }

  it should "fetch weather data for a city and save it to a file" in {
    val cityName = "London"
    val responseFuture = OpenWeatherMapApiClient.fetchData(cityName)

    responseFuture.map { response =>
      val fileName = s"$cityName.json"
      Files.write(Paths.get(fileName), response.getBytes)
      succeed
    }
  }
}
