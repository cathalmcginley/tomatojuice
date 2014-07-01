package org.gnostai.tomatojuice2.db.actors

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.ActorLogging

class DatabaseActor extends Actor with ActorLogging with PomodoroDbActorModule {
  
  val connectionPoolExt = BoneConnectionPoolExtension(context.system)
  
  val conn = connectionPoolExt.pool.getConnection()
  
  val pomodoroActor = context.actorOf(Props(new PomodoroDbActor(conn)))
  
  override def receive: Receive = {
    case "get" =>
      println("got")
      val stmt = conn.createStatement()
      val rslt = stmt.executeQuery("SELECT name, description FROM project ORDER BY id")
      while (rslt.next()) {
        println(rslt.getString(1) + ":\t" + rslt.getString(2))
      }
      rslt.close()
      stmt.close()
      //conn.close()
    case "create" =>
      log.info("create, before send")
      pomodoroActor ! CreateNewPomodoro(25)
      log.info("create, after send")
    case PomodoroCreated =>
      log.info("created")
  }

  



}



object DatabaseActor {
  
  def main(args: Array[String]): Unit = {
    val sys = ActorSystem("DBtesting")
    val dbActor = sys.actorOf(Props(new DatabaseActor))
    dbActor ! "get"
    dbActor ! "create"
    dbActor ! "create"
//    Thread.sleep(500)
//    sys.shutdown()
  }
  
}