/*
 * Copyright (C) 2014 Cathal Mc Ginley
 *
 * This file is part of TomatoJuice, a Pomodoro timer-tracker for GNOME.
 *
 * TomatoJuice is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * TomatoJuice is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TomatoJuice; see the file COPYING. If not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1301 USA.
*/

package org.gnostai.tomatojuice.ui

import akka.actor.ActorRef
import scala.concurrent.Future
import org.gnostai.tomatojuice.core.CoreDomainModule

trait StatusIconModule extends CoreDomainModule with UIFacadeModule with NoteDialogModule {
  
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