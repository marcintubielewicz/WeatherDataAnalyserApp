package model.DAO

import java.sql.{DriverManager, ResultSet}

object DatabaseConnection extends App {
  println("Postgres connector")

  classOf[org.postgresql.Driver]
  val con_str = "jdbc:postgresql://localhost:5432/weatherappdata?marcintubielewicz=DB_USER"
  val conn = DriverManager.getConnection(con_str)
  try {
    val stm = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)

    val rs = stm.executeQuery("SELECT * from weather_data")

    while(rs.next) {
      println(rs.getString("quote"))
    }
  } finally {
    conn.close()
  }
}