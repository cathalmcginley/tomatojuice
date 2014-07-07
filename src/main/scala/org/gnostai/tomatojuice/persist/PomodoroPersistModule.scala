package org.gnostai.tomatojuice.persist

import akka.actor._
import akka.pattern.pipe
import scala.concurrent.Future
import org.gnostai.tomatojuice.core.CoreDomainModule

trait PomodoroPersistModule extends CoreDomainModule {

  type POMODORO_ID

  object PomodoroPersist {

    case class CreateNewPomodoro(durationMinutes: Int, origSender: ActorRef)
    case class PomodoroCreated(pomodoroId: POMODORO_ID)
    case class PomodoroCompleted(pomodoroId: POMODORO_ID)
    case object Continue

    case class SavePomodoroNote(pomodoroId: POMODORO_ID,
      project: Project,
      text: String)

  }

  abstract class PomodoroPersistActor extends Actor with ActorLogging with UnboundedStash {

    private implicit val dispatcher = context.system.dispatcher

    def receive: Receive = free

    def busy: Receive = {
      case PomodoroPersist.Continue =>
        context.become(free)
        unstashAll()
      case x =>
        stash()
    }

    def free: Receive = {
      case PomodoroPersist.CreateNewPomodoro(mins, origSender) =>
        implicit val dispatcher = context.system.dispatcher
        val origSender = sender
        context.become(busy)
        asyncRecordNewPomodoro(mins, origSender) map { _ => PomodoroPersist.Continue } pipeTo self
      case PomodoroPersist.PomodoroCompleted(id) =>
        implicit val dispatcher = context.system.dispatcher
        val origSender = sender
        context.become(busy)
        asyncMarkPomodoroCompleted(id) map { _ => PomodoroPersist.Continue } pipeTo self
        
      case PomodoroPersist.SavePomodoroNote(id, project, note) =>
        implicit val dispatcher = context.system.dispatcher
        val origSender = sender
        context.become(busy)
        asyncAddPomodoroNote(id, project, note) map { _ => PomodoroPersist.Continue } pipeTo self

    }

    def asyncRecordNewPomodoro(duration: Int, origSender: ActorRef): Future[POMODORO_ID]
    def asyncMarkPomodoroCompleted(id: POMODORO_ID): Future[Boolean]
    
    
    def asyncAddPomodoroNote(id: POMODORO_ID, project: Project, text: String): Future[Boolean]
    
  }
}
