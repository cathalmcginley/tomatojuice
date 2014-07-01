package org.gnostai.tomatojuice2.db.actors

import akka.actor.Actor
import java.sql.Connection
import java.sql.Timestamp
import scala.concurrent.Future
import akka.actor.ActorLogging
import akka.actor.UnboundedStash

import akka.pattern.pipe

trait PomodoroDbActorModule {

  case class CreateNewPomodoro(durationMinutes: Int)
  case object PomodoroCreated
  case object Continue

  class PomodoroDbActor(conn: Connection) extends Actor with ActorLogging with UnboundedStash {

    private implicit val dispatcher = context.system.dispatcher 
    
    private val insertSql = "INSERT INTO pomodoro (start_time, duration_minutes, completed)" +
      " VALUES (?, ?, FALSE)"
    lazy val insertStmt = conn.prepareStatement(insertSql)

    def receive: Receive = free

    def busy: Receive = {
      case c @ CreateNewPomodoro(mins) =>
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
      case c @ CreateNewPomodoro(mins) =>
        log.info("  << in free " + c)
        implicit val dispatcher = context.system.dispatcher
        val origSender = sender
        log.info("becoming busy")
        context.become(busy)
        log.info("before future")
        futureSql(mins) map { _ => Continue } pipeTo self
        log.info("after future")
    }

    def futureSql(mins: Int) = Future {
      log.info("before sql")
      insertStmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()))
      insertStmt.setInt(2, mins)
      val count = insertStmt.executeUpdate()
      log.info("after sql, update executed")
      count
    }

  }

}