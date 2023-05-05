package model

import org.json4s._
import org.json4s.jackson.JsonMethods._
import scalaj.http._

case class OpenWeatherAPI(city: String, temperature: BigDecimal, humidity: Double)

object OpenWeatherAPI {
  implicit val formats: DefaultFormats.type = DefaultFormats

  val baseUrl = "https://api.openweathermap.org/data/2.5/weather"
  val apiKey = "ab04e018aaeb851b315e111377c58817"

  def getWeatherData(cityName: String): Option[OpenWeatherAPI] = {
    val response = Http(baseUrl).params(Seq("q" -> cityName, "appid" -> apiKey)).asString
    if (response.isSuccess) {
      val json = parse(response.body)
      val temperature = BigDecimal((json \ "main" \ "temp").extract[Double] - 273.15)setScale(2, BigDecimal.RoundingMode.HALF_UP)
      val humidity = (json \ "main" \ "humidity").extract[Double]
      Some(OpenWeatherAPI(cityName, temperature, humidity))
    } else {
      println(s"Failed to get weather data for $cityName")
      None
    }
  }
}
