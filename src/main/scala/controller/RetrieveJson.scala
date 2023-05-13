package controller

class RetrieveJson {
  def readDataFromJsonFileAndSaveItToDatabase(): Unit = {
    val fileName = "London.json"
    val json = scala.io.Source.fromFile(fileName).mkString
    println(json)
  }
}
