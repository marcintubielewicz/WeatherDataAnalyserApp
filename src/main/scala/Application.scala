import model.WeatherData

object Application {
  def main(args: Array[String]): Unit = {

    val weatherDataOpt = WeatherData.getWeatherData("Zielonka, PL")
    weatherDataOpt match {
      case Some(weatherData) => {
        println(s"Actual temperature in ${weatherData.city}: ${weatherData.temperature}) C \nhumidity is: ${weatherData.humidity} %")
      }
      case None => println("Failed to get weather data")
    }
  }
}

