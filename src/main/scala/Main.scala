import controller.OpenWeatherMapApiClient

object Main{
  def main(args: Array[String]): Unit = {
    println("Starting")

    val jsonDataZielonka = OpenWeatherMapApiClient.fetchAndSaveData("Zielonka")
    val jsonDataLondon = OpenWeatherMapApiClient.fetchAndSaveData("London")
    val jsonDataBydgoszcz = OpenWeatherMapApiClient.fetchAndSaveData("Bydgoszcz")


  }
}
