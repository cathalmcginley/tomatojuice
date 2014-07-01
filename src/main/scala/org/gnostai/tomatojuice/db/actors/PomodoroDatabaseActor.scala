package org.gnostai.tomatojuice.db.actors

import akka.actor.Actor
import java.sql.Connection
import java.sql.Timestamp
import scala.concurrent.Future
import akka.actor.ActorLogging
import akka.actor.UnboundedStash
import akka.pattern.pipe
import akka.actor.ActorRef
import org.gnostai.tomatojuice.persist.PomodoroPersistModule


trait PomodoroDatabaseModule extends PomodoroPersistModule {

  class PomodoroDatabaseActor(conn: Connection) extends PomodoroPersistActor{

    private implicit val dispatcher = context.system.dispatcher 
    
    private val insertSql = "INSERT INTO pomodoro (start_time, duration_minutes, completed)" +
      " VALUES (?, ?, FALSE)"
    lazy val insertStmt = conn.prepareStatement(insertSql)


    override def asyncRecordNewPomodoro(mins: Int, origSender: ActorRef) = Future {      
      insertStmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()))
      insertStmt.setInt(2, mins)
      val count = insertStmt.executeUpdate()
      val rslt = insertStmt.getGeneratedKeys()
      if (count == 1) {
        rslt.next()      
        val newId = rslt.getInt(1)
        origSender ! PomodoroCreated(newId)
      }
      count
    }

  }

}