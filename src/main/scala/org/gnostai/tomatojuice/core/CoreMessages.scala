package org.gnostai.tomatojuice.core

import akka.actor.ActorRef

trait CoreMessagesModule extends CoreDomainModule {
  
  object CoreMessages {
    
    /** sent to main app by gui; instruct it to notify the tracker  */
    case object StartTimer 
    
    /** sent to main app by tracker; ask main to record this is db */
    case object NewPomodoroStarted
    
    /** sent to main app by gui again; ask main to update db  */
    case object ConfirmPomodoroCompleted
    
   
   
    /**
     * sent to main to link up parts of the UI actor system with
     * the pomodoro tracker actor
     */
    case class RegisterPomodoroListener(listener: ActorRef) // HACK
    
    
    case object GetProjectList
    case class SendProjectList(orig: ActorRef)
    case class ProjectList(projects: Iterable[Project])
    
    case object Quit
  }

}