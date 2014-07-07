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

package org.gnostai.tomatojuice.db.actors

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.ActorContext
import org.gnostai.tomatojuice.persist.PersistModule
import akka.event.LoggingReceive

trait DatabaseModule extends PersistModule with PomodoroDatabaseModule
  with ProjectDatabaseActorModule {

  def createDBMainActor(context: ActorContext, name: String): ActorRef

  abstract class DatabaseActor extends PersistActor {

    def pomodoroActor: ActorRef
    def projectActor: ActorRef

    // HACK duplication!!!
    override def receive: Receive = LoggingReceive {
      case RecordPomodoroStart =>
        pomodoroActor ! PomodoroPersist.CreateNewPomodoro(25, sender)
      case RecordPomodoroCompleted(id) =>
        pomodoroActor ! PomodoroPersist.PomodoroCompleted(id)
      case RecordNewProject(name, description, icon) =>
        projectActor ! ProjectPersist.CreateNewProject(name, description, icon, sender)
        
      
      case x @ PomodoroPersist.SavePomodoroNote(id, project, note) =>
        pomodoroActor ! x 
      case x @ CoreMessages.SendProjectList(origSender) =>
        projectActor ! x
    }

  }
}