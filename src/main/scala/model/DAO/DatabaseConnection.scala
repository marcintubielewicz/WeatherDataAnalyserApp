package model.DAO

import java.sql.{DriverManager, ResultSet}

/**
 * Establishes a connection to a PostgreSQL database and retrieves data from the "weather_data" table.
 * The retrieved data is then printed to the console.
 */
object DatabaseConnection extends App {

  classOf[org.postgresql.Driver]
  val con_str = "jdbc:postgresql://localhost:5432/weatherappdata?marcintubielewicz=DB_USER"
  val conn = DriverManager.getConnection(con_str)
  try {
    val stm = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)

    val rs = stm.executeQuery("SELECT * from weather_data")

    while (rs.next) {
      println("id: " + rs.getString("id") +
        ", city_id: " + rs.getString("city_id") +
        ", coord_lon: " + rs.getDouble("coord_lon") +
        ", coord_lat: " + rs.getDouble("coord_lat") +
        ", city_name: " + rs.getString("city_name")+
        ",main_temp: " + rs.getDouble("main_temp"))
      println()
    }
  } finally {
    conn.close()
  }
}