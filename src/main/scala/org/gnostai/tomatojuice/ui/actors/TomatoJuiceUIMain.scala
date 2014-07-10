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

package org.gnostai.tomatojuice.ui.actors

import akka.actor._
import scala.concurrent.Future
import akka.event.LoggingReceive
import org.gnostai.tomatojuice.ui.NoteDialogModule
import org.gnostai.tomatojuice.ui.StatusIconModule

trait TomatoJuiceUIMainModule extends StatusIconActorModule with StatusIconModule
  with PomodoroNoteDialogActorModule with NoteDialogModule {

  case object StartUp
  

  class TomatoJuiceUIMain(mainApp: ActorRef) extends Actor with ActorLogging {

    val statusIcon = context.actorOf(Props(new StatusIconActor(mainApp)), "PomodoroCountdownIcon")
    val noteDialog = context.actorOf(Props(new PomodoroNoteDialogActor(mainApp)), "PomodoroNoteDialog")
    
    def receive: Receive = {
      case StartUp =>
        import context.dispatcher
        Future {
          initializeGui(context.system, self)
        } onSuccess {
          case handle =>
            context.become(activeGuiRunning(handle))
        } 
        
      case x => log.info("main: " + x)
    }

    def activeGuiRunning(guiHandle: GUI_HANDLE): Receive = LoggingReceive {
      case GuiActivated(handle) =>
        statusIcon ! DisplayInitialStatusIcon(handle)
      case PomodoroNoteDialog.PopUpNoteDialog =>
        val appHandle = ApplicationHandle(context.system, self, guiHandle)
        noteDialog ! PomodoroNoteDialog.PopUpDialog(appHandle)
        
      case PomodoroNoteDialog.PopDownNoteDialog =>
        val appHandle = ApplicationHandle(context.system, self, guiHandle)
        noteDialog ! PomodoroNoteDialog.PopDownDialog(appHandle)
  
        
      case PomodoroNoteDialog.NoteSaved =>
        log.info("emitting a pseudo-click here...")
        statusIcon ! StatusIconActivated
        
      case x @ PomodoroNoteDialog.DialogClosing =>
        noteDialog ! PomodoroNoteDialog.DialogClosing
      case CoreMessages.GetProjectList =>
        println("get project list request from " + sender.path)
        mainApp ! CoreMessages.SendProjectList(sender)
      case x => 
        log.warning("got unexpected message " + x + "; sending to mainApp")
        mainApp ! x
    }
    
  }

}