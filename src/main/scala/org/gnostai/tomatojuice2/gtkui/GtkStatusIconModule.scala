package org.gnostai.tomatojuice2.gtkui

import org.gnostai.tomatojuice2.ui.StatusIconModule
import org.gnome.gdk
import org.gnome.gtk
import akka.actor.ActorRef
import scala.concurrent.Future
import scala.concurrent.Promise

trait GtkStatusIconModule extends GtkUIFacadeModule with StatusIconModule {

  class GtkStatusIconFacade(iconActor: ActorRef, handle: GUI_HANDLE) extends StatusIconFacade {

    println("> what now... 1 ?")

    val ImageDir = "src/main/resources/icons"

    println("> what now... 2 ?")

    val icons = new PomodoroIcons(ImageDir)

    println("> what now... 3 ?")
    println("> what now...?")

    val statusIcon: gtk.StatusIcon = buildStatusIcon

    def sessionBegins() {
      println("(): NO ACTION")
    }

    def breakBegins() {
      println("breakBegins(): NO ACTION")
    }

    def longBreakBegins() {
      println("longBreakBegins(): NO ACTION")
    }

    def showMinutesRemaining(minutesRemaining: Int) {
      println("showMinutesRemaining(): NO ACTION")
    }

    def hintTimeRemaining(minutes: Int, seconds: Int) {
      println("hintTimeRemaining(): NO ACTION")
    }

    def timerCompleted() {
      println("timerCompleted(): NO ACTION")
    }

    private def buildStatusIcon = {

      println("buildStatusIcon")

      //      val mainWin = new gtk.Window
      //        mainWin.add(new gtk.Label("test"))
      //        mainWin.showAll()
      //
      //        handle.addWindow(mainWin)
      //        mainWin.present()

      var status2: gtk.StatusIcon = null
      println("ready for safety")
      //safely {
      println("saef??")
      val status = new gtk.StatusIcon(icons.initial) //icons.initial)
      status.setVisible(true)
      println(status.isEmbedded())

      status.setVisible(true)
      println(status.isEmbedded())

      status.connect(new gtk.StatusIcon.Activate() {
        def onActivate(icon: gtk.StatusIcon) {
          println("activate")
          iconActor ! StatusIconActivated
        }
      })

      println("foo")

      status2 = status
      //}
      status2
    }

  }

  type STATUS_ICON = GtkStatusIconFacade

  override def constructStatusIcon(iconActor: ActorRef, handle: GUI_HANDLE): Future[STATUS_ICON] = {
    println("constructStatusIcon")
    //    safely {
    //        // vv TEMP REMOVE vv
    //             val pixbuf = new gdk.Pixbuf("src/main/resources/icons/green-led-on.png")
    //        val status = new gtk.StatusIcon(pixbuf)
    //        status.setVisible(true)
    //        println(">> embedded?    " + status.isEmbedded())
    //        // ^^ TEMP REMOVE ^^
    //    }



    import scala.concurrent.ExecutionContext.Implicits.global
    val facadePromise = Promise[STATUS_ICON]
    Future {
      safely {
        val iconFacade = new GtkStatusIconFacade(iconActor, handle)
        facadePromise success (iconFacade)
      }
    }

    facadePromise.future

  }

  class GtkStatusIconMenuFacade extends StatusIconMenuFacade {
    def popUp() {
      println("popUp(): NO ACTION")
    }
  }

  type STATUS_ICON_MENU = GtkStatusIconMenuFacade

}