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

package org.gnostai.tomatojuice.gtkui

import org.gnostai.tomatojuice.actors.TomatoJuiceMainModule
import akka.actor._
import org.gnostai.tomatojuice.jorbisui.JOrbisAudioNotificationModule
import org.gnostai.tomatojuice.mysqldb.MySQLDatabaseModule
import org.gnostai.tomatojuice.ui.actors.TomatoJuiceUIMainModule
import org.gnostai.tomatojuice.ui.actors.StatusIconActorModule
import org.gnostai.tomatojuice.ui.actors.StatusIconActorModule
import org.gnostai.tomatojuice.ui.StatusIconModule
import org.gnostai.tomatojuice.ui.NoteDialogModule
import org.gnostai.tomatojuice.ui.actors.PomodoroNoteDialogActorModule
import org.gnostai.tomatojuice.core.ProductionCoreModule
import org.gnostai.tomatojuice.ui.AudioNotificationModule
import org.gnostai.tomatojuice.actors.PomodoroCountdownModuleImpl

trait UI extends StatusIconModule 
  with NoteDialogModule
  with AudioNotificationModule
  
trait GtkUI extends UI
  with GtkUIFacadeModule
  with GtkStatusIconModule
  with GtkPomodoroNoteDialogModule

object GtkUIMain extends GtkUI
  with ProductionCoreModule
  with JOrbisAudioNotificationModule
  with PomodoroCountdownModuleImpl // HACK
  with TomatoJuiceMainModule
  with StatusIconActorModule  
  with PomodoroNoteDialogActorModule
  with TomatoJuiceUIMainModule
  
  with MySQLDatabaseModule {

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("TomatoJuice")
    val mainApp = system.actorOf(Props(new TomatoJuiceMainActorImpl), "TomatoJuice")
    mainApp ! TomatoJuiceMain.StartUI
  }
}



