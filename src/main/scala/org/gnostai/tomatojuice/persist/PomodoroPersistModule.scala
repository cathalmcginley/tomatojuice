package org.gnostai.tomatojuice.persist

import akka.actor._
import akka.pattern.pipe
import scala.concurrent.Future

trait PomodoroPersistModule {
  
  case class CreateNewPomodoro(durationMinutes: Int, origSender: ActorRef)
  case class PomodoroCreated(pomodoroId: Int)
  case object Continue
  
  
  abstract class PomodoroPersistActor extends Actor with ActorLogging with UnboundedStash {

    private implicit val dispatcher = context.system.dispatcher 
    
    def receive: Receive = free

    def busy: Receive = {
      case Continue =>
        context.become(free)
        unstashAll()
      case x =>
        stash()
    }

    def free: Receive = {
      case CreateNewPomodoro(mins, origSender) =>
        implicit val dispatcher = context.system.dispatcher
        val origSender = sender
        context.become(busy)
        asyncRecordNewPomodoro(mins, origSender) map { _ => Continue } pipeTo self
    }

    def asyncRecordNewPomodoro(duration: Int, origSender: ActorRef): Future[Int]
    
  }
}
