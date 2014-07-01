package org.gnostai.tomatojuice.db.actors

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.ActorContext
import org.gnostai.tomatojuice.persist.PersistModule
import akka.event.LoggingReceive

trait DatabaseModule extends PersistModule with PomodoroDatabaseModule {

  
  
  def createDBMainActor(context: ActorContext, name: String): ActorRef
  
  abstract class DatabaseActor extends PersistActor {

    def pomodoroActor: ActorRef

    override def receive: Receive = LoggingReceive {
      case RecordPomodoroStart =>
        pomodoroActor ! CreateNewPomodoro(25, sender)
      case RecordPomodoroCompleted(id) =>
        pomodoroActor ! PomodoroCompleted(id)
    }

  }
}