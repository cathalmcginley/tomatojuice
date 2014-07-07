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

package org.gnostai.tomatojuice.persist

import akka.actor._
import org.gnostai.tomatojuice.core.CoreMessagesModule


trait PersistModule  extends PomodoroPersistModule with ProjectPersistModule with CoreMessagesModule {

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
      case x @ CoreMessages.SendProjectList(origSender) =>
        projectActor ! x 
    }

  }
}