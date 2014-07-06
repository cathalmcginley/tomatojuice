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

object GtkUIMain extends TomatoJuiceMainModule
  with StatusIconActorModule
  with StatusIconModule
  with GtkUIFacadeModule
  with GtkStatusIconModule
  with GtkPomodoroNoteDialogModule
  with NoteDialogModule
  with PomodoroNoteDialogActorModule
  with TomatoJuiceUIMainModule
  with JOrbisAudioNotificationModule
  with MySQLDatabaseModule {

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("TomatoJuice")
    val mainApp = system.actorOf(Props(new TomatoJuiceMainActor), "TomatoJuice")
    println(mainApp)
    mainApp ! TomatoJuiceMain.StartUI
  }
}



