package views

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import scala.collection.mutable
import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

/**
 * The WeatherDataPrinter object provides functionality to print average, minimum, and maximum monthly temperatures by country and city.
 * It starts an HTTP server that exposes an endpoint to display the temperature data in an HTML table.
 */
object WeatherDataPrinter {
  /**
   * The entry point of the WeatherDataPrinter application.
   * It starts an HTTP server and binds it to a specific endpoint.
   *
   * @param args The command line arguments (not used).
   */
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("web-server")
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    val averages = new Averages

    val route: Route = get {
      path("average-min-max-temperature") {
        val results: List[(String, String, Double, Double, Double, Int)] = averages.invokeAverageMinMaxMonthlyTempByCountry()

        val html = new mutable.StringBuilder
        html.append("<html><body>")
        html.append("<h1>Average Min Max Monthly Temperature By Country</h1>")
        html.append("<table>")
        html.append("<tr><th>Country</th><th>City</th><th>Average Temperature</th><th>Min Temperature</th><th>Max Temperature</th><th>Month</th></tr>")

        results.foreach { case (country, city, averageTemperature, minTemperature, maxTemperature, month) =>
          html.append(s"<tr><td>$country</td><td>$city</td><td>$averageTemperature</td><td>$minTemperature</td><td>$maxTemperature</td><td>$month</td></tr>")
        }

        html.append("</table>")
        html.append("</body></html>")

        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, html.toString()))
      }
    }

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)

    println("Server online at http://localhost:8080/average-min-max-temperature")
    StdIn.readLine()

    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
