/**
*The OpenWeatherMapApiClientSpec class is a ScalaTest for testing the OpenWeatherMapApiClient class.
*It extends AsyncFlatSpec and uses Matchers for defining test cases.
 */
package controller

import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

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

}
