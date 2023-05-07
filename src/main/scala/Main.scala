import controller.OpenWeatherMapApiClient

object Main{
  def main(args: Array[String]): Unit = {
    println("Starting")

    val jsonDataZielonka = OpenWeatherMapApiClient.fetchAndPrintData("Zielonka")
    val jsonDataLondon = OpenWeatherMapApiClient.fetchAndPrintData("London")
    val jsonDataBydgoszcz = OpenWeatherMapApiClient.fetchAndPrintData("Bydgoszcz")
  }
}
