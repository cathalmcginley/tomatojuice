package org.gnostai.tomatojuice.db.actors

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.ActorContext

trait TomatoJuiceDatabaseModule extends PomodoroDatabaseActorModule {

  case object RecordPomodoroStart
  
  def createDBMainActor(context: ActorContext, name: String): ActorRef
  
  abstract class TomatoJuiceDBMain extends Actor with ActorLogging {

    def pomodoroActor: ActorRef

    override def receive: Receive = {
      case RecordPomodoroStart =>
        log.info("create, before send")
        pomodoroActor ! CreateNewPomodoro(25, sender)
        log.info("create, after send")
      case PomodoroCreated =>
        log.info("created")
    }

  }
}