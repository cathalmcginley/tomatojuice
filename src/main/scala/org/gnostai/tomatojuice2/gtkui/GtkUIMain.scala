package org.gnostai.tomatojuice2.gtkui

import org.gnostai.tomatojuice2.ui.actors.TomatoJuiceMainModule

import akka.actor._

object GtkUIMain extends TomatoJuiceMainModule
  with GtkUIFacadeModule
  with GtkStatusIconModule {

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("a")
    val mainApp = system.actorOf(Props(new TomatoJuiceMain))
    mainApp ! StartUp
  }
}

