package org.gnostai.tomatojuice.gtkui

import org.gnostai.tomatojuice.actors.TomatoJuiceMainModule
import akka.actor._
import org.gnostai.tomatojuice.jorbisui.JOrbisAudioNotificationModule
import org.gnostai.tomatojuice.mysqldb.MySQLDatabaseModule
import org.gnostai.tomatojuice.ui.actors.TomatoJuiceUIMainModule

object GtkUIMain extends TomatoJuiceMainModule
  with TomatoJuiceUIMainModule
  with GtkUIFacadeModule
  with GtkStatusIconModule 
  with JOrbisAudioNotificationModule 
  with MySQLDatabaseModule {

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("TomatoJuice")
    val mainApp = system.actorOf(Props(new TomatoJuiceMainActor), "TomatoJuice")
    println(mainApp)
    mainApp ! TomatoJuiceMain.StartUI
  }
}



