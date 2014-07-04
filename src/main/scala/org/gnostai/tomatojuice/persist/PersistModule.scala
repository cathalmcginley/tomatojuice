package org.gnostai.tomatojuice.persist

import akka.actor._

trait PersistModule  extends PomodoroPersistModule with ProjectPersistModule {

  case object RecordPomodoroStart
  case class RecordPomodoroCompleted(pomodoroId: POMODORO_ID)
  case class RecordNewProject(name: String,
      description: String,
      icon: Option[ImageData])
  

  
  def createDBMainActor(context: ActorContext, name: String): ActorRef
  
  abstract class PersistActor extends Actor with ActorLogging {

    def pomodoroActor: ActorRef

    override def receive: Receive = {
      case RecordPomodoroStart =>
        pomodoroActor ! CreateNewPomodoro(25, sender)
      case RecordPomodoroCompleted(id) =>
        pomodoroActor ! CreateNewPomodoro(25, sender)
      case PomodoroCreated =>
        log.info("created")
    }

  }
}