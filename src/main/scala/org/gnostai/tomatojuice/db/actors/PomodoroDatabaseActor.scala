package org.gnostai.tomatojuice.db.actors

import akka.actor.Actor
import java.sql.Connection
import java.sql.Timestamp
import scala.concurrent.Future
import akka.actor.ActorLogging
import akka.actor.UnboundedStash
import akka.pattern.pipe
import akka.actor.ActorRef


trait PomodoroDatabaseActorModule {

  case class CreateNewPomodoro(durationMinutes: Int, origSender: ActorRef)
  case object PomodoroCreated
  case object Continue

  class PomodoroDatabaseActor(conn: Connection) extends Actor with ActorLogging with UnboundedStash {

    private implicit val dispatcher = context.system.dispatcher 
    
    private val insertSql = "INSERT INTO pomodoro (start_time, duration_minutes, completed)" +
      " VALUES (?, ?, FALSE)"
    lazy val insertStmt = conn.prepareStatement(insertSql)

    def receive: Receive = free

    def busy: Receive = {
      case c @ CreateNewPomodoro(mins, origSender) =>
        log.info("  << in busy " + c)
        log.info("stashing " + c)
        stash()
      case Continue =>
        log.info("  << in busy " + Continue)
        log.info("becoming free")
        context.become(free)
        log.info("unstashing all")
        unstashAll()
    }

    def free: Receive = {
      case c @ CreateNewPomodoro(mins, origSender) =>
        log.info("  << in free " + c)
        implicit val dispatcher = context.system.dispatcher
        val origSender = sender
        log.info("becoming busy")
        context.become(busy)
        log.info("before future")
        futureSql(mins, origSender) map { _ => Continue } pipeTo self
        log.info("after future")
    }

    def futureSql(mins: Int, origSender: ActorRef) = Future {
      log.info("before sql")
      insertStmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()))
      insertStmt.setInt(2, mins)
      val count = insertStmt.executeUpdate()
      log.info("after sql, update executed")
      origSender ! PomodoroCreated
      count
    }

  }

}