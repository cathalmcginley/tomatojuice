package org.gnostai.tomatojuice.db.actors

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.ActorContext
import org.gnostai.tomatojuice.persist.PersistModule
import akka.event.LoggingReceive

trait DatabaseModule extends PersistModule with PomodoroDatabaseModule
  with ProjectDatabaseActorModule {

  def createDBMainActor(context: ActorContext, name: String): ActorRef

  abstract class DatabaseActor extends PersistActor {

    def pomodoroActor: ActorRef
    def projectActor: ActorRef

    // HACK duplication!!!
    override def receive: Receive = LoggingReceive {
      case RecordPomodoroStart =>
        pomodoroActor ! PomodoroPersist.CreateNewPomodoro(25, sender)
      case RecordPomodoroCompleted(id) =>
        pomodoroActor ! PomodoroPersist.PomodoroCompleted(id)
      case RecordNewProject(name, description, icon) =>
        projectActor ! ProjectPersist.CreateNewProject(name, description, icon, sender)
      case x @ CoreMessages.SendProjectList(origSender) =>
        projectActor ! x
    }

  }
}