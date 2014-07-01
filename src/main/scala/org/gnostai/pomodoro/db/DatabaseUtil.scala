package org.gnostai.pomodoro.db


import java.sql.Connection
import java.sql.DriverManager
import java.util.Properties

object DatabaseUtil {

  val userName = "tomato";
  val password = "BappyoShno";
  val dbms = "mysql";
  val serverName = "localhost";
  val portNumber = 3306;
  val dbName = "tomatojuice";

  lazy val conn = createConnection()
  
  def getConnection() = conn
    
  def createConnection(): Connection = {
    val dbUri = "jdbc:" + this.dbms + "://" +
      this.serverName + ":" + this.portNumber + "/" + dbName //// + "?profileSQL=true"
    val connectionProps = new Properties();
    connectionProps.put("user", this.userName);
    connectionProps.put("password", this.password);
    DriverManager.getConnection(dbUri, connectionProps);
  }

  
  def main(args: Array[String]): Unit = {
    println(DatabaseUtil.createConnection().getMetaData())
  }
}