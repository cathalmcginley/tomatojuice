package org.gnostai.tomatojuice.db.actors

import akka.actor.Actor
import java.sql.Connection
import java.sql.Timestamp
import java.sql.Statement
import scala.concurrent.Future
import akka.actor.ActorLogging
import akka.actor.UnboundedStash
import akka.pattern.pipe
import akka.actor.ActorRef
import org.gnostai.tomatojuice.persist.PomodoroPersistModule


trait PomodoroDatabaseModule extends PomodoroPersistModule {

  case class PomodoroDbId(val dbId: Int)
  
  type POMODORO_ID = PomodoroDbId 
  
  class PomodoroDatabaseActor(conn: Connection) extends PomodoroPersistActor {

    private implicit val dispatcher = context.system.dispatcher 
    
    private val insertSql = "INSERT INTO pomodoro (start_time, duration_minutes, completed)" +
      " VALUES (?, ?, FALSE)"
    lazy val insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)


    private val updateSql ="UPDATE pomodoro SET completed=? WHERE id=?"
    lazy val updateStmt = conn.prepareStatement(updateSql) 
    
    override def asyncRecordNewPomodoro(mins: Int, origSender: ActorRef) = Future {      
      insertStmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()))
      insertStmt.setInt(2, mins)
      val count = insertStmt.executeUpdate()
      val rslt = insertStmt.getGeneratedKeys()
      if (count == 1) {
        rslt.next()      
        val newId = rslt.getInt(1)
        log.info("orig sender " + origSender)
        val grandparent = context.actorSelection("../../")
        
        val main = context.actorFor("akka://TomatoJuice/user/TomatoJuice")
        log.info("main " + main)
        main ! PomodoroCreated(PomodoroDbId(newId))
      }
      count
    }

    override def asyncMarkPomodoroCompleted(id: POMODORO_ID) = Future {
      println("updating id " + id.dbId)
      updateStmt.setBoolean(1, true)
      updateStmt.setInt(2, id.dbId)
      val count = updateStmt.executeUpdate()
      println("count " + count)
      println(updateStmt)
      (count == 1)
    }
    
    
  }

}