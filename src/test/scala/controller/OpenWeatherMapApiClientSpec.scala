/**
*The OpenWeatherMapApiClientSpec class is a ScalaTest for testing the OpenWeatherMapApiClient.scala class.
*It extends AsyncFlatSpec and uses Matchers for defining test cases.
 */
package controller

import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import java.nio.file.{Files, Paths}
import scala.concurrent.Future
import scala.language.postfixOps

class OpenWeatherMapApiClientSpec extends AsyncFlatSpec with Matchers {

  // test case fetch data from given city and check the response contains the city name
  it should "fetch weather data for a city" in {
    val cityName = "Bydgoszcz"
    val responseFuture = OpenWeatherMapApiClient.fetchData(cityName)

    responseFuture.map { response =>
      response should include(cityName)
    }
  }
  // test case fetch data for given city name and print it to the console
  it should "print weather data for a city" in {
    val cityName = "Zielonka"
    val response = OpenWeatherMapApiClient.fetchAndPrintData(cityName)
    Thread.sleep(2000)
    succeed
  }

  // test case fetch data for given city name and save it to a file
  it should "fetch weather data for a city and save it to a file" in {
    val cityName = "Osaka"
    val responseFuture: Future[String] = OpenWeatherMapApiClient.fetchData(cityName)

    responseFuture.flatMap { response =>
      val fileName = s"$cityName.json"
      val filePath = Paths.get(fileName)
      val writeFileFuture = Future {
        Files.write(filePath, response.getBytes)
      }

      writeFileFuture.map(_ => succeed)
    }
  }
}
