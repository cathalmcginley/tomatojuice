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
    def projectActor: ActorRef

    override def receive: Receive = {
      case RecordPomodoroStart =>
        pomodoroActor ! PomodoroPersist.CreateNewPomodoro(25, sender)
      case RecordPomodoroCompleted(id) =>
        pomodoroActor ! PomodoroPersist.CreateNewPomodoro(25, sender)
      case RecordNewProject(name, desc, icon) =>
        log.info("record new project " + name)
        projectActor ! ProjectPersist.CreateNewProject(name, desc, icon, sender)
      case PomodoroPersist.PomodoroCreated =>
        log.info("created")
    }

  }
}