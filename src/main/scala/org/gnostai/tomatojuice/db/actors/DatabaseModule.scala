package org.gnostai.tomatojuice.db.actors

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.ActorContext
import org.gnostai.tomatojuice.persist.PersistModule

trait DatabaseModule extends PersistModule with PomodoroDatabaseModule {

  
  
  def createDBMainActor(context: ActorContext, name: String): ActorRef
  
  abstract class DatabaseActor extends PersistActor {

    def pomodoroActor: ActorRef

    override def receive: Receive = {
      case RecordPomodoroStart =>
        pomodoroActor ! CreateNewPomodoro(25, sender)
      case PomodoroCreated =>
        log.info("created")
    }

  }
}