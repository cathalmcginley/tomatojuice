package org.gnostai.tomatojuice.ui

import akka.actor.ActorRef
import scala.concurrent.Future
import org.gnostai.tomatojuice.core.CoreDomainModule

trait StatusIconModule extends UIFacadeModule with CoreDomainModule {
  
  case class DisplayInitialStatusIcon(handle: GUI_HANDLE) extends Message
  case object StatusIconActivated extends Message
  case object HideStatusIcon extends Message
  

  
  abstract class StatusIconFacade extends UIFacade {
    
    def sessionBegins(): Unit
    
    def breakBegins(): Unit
    
    def longBreakBegins(): Unit
    
    
    def showStartIcon(countdown: CountdownTimer)
    
    def showMinutesRemaining(minutesRemaining: Int, countdown: CountdownTimer)
    
    def hintTimeRemaining(minutes: Int, seconds: Int)
    
    def hintMessage(message: String)
    
    
    def timerCompleted(): Unit
    
  }

  type STATUS_ICON <: StatusIconFacade

  def constructStatusIcon(iconActor: ActorRef, handle: GUI_HANDLE): Future[STATUS_ICON]

  
  abstract class StatusIconMenuFacade extends UIFacade {
    def popUp(): Unit
  }

  type STATUS_ICON_MENU <: StatusIconMenuFacade

}