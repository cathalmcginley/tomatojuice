package org.gnostai.tomatojuice.gtkui

import org.gnostai.tomatojuice.ui.actors.TomatoJuiceUIMainModule

import akka.actor._

object GtkUIMain extends TomatoJuiceUIMainModule
  with GtkUIFacadeModule
  with GtkStatusIconModule {

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("a")
    val mainApp = system.actorOf(Props(new TomatoJuiceUIMain))
    mainApp ! StartUp
  }
}

