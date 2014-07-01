package org.gnostai.tomatojuice2.ui

import akka.actor.ActorRef
import akka.actor.ActorSystem

trait UIFacadeModule {

  case class GuiActivated(handle: GUI_HANDLE)

  trait Message
  
  trait UIFacade
  
  type GUI_HANDLE
  
  case class ApplicationHandle(system: ActorSystem,
      mainApp: ActorRef,
      guiHandle: GUI_HANDLE)
  
  def initializeGui(system: ActorSystem, mainApp: ActorRef): GUI_HANDLE   
  
  def shutdownGui(mainApp: ActorRef, handle: GUI_HANDLE)
  
}