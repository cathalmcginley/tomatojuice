package org.gnostai.pomodoro.gtkui

import akka.actor.ActorRef
import org.gnome.gtk._

import org.gnome.gdk.Event
import org.gnome.gdk.Pixbuf
import org.gnome.notify.Notification
import org.gnome.notify.Notify
import tomatojuice.JavaSounds

class MainWindow(mainWindowActor: ActorRef) extends GtkUIFacade {

  val window = buildWindow

  

  def activateGui() {

    window.present()

  }

  def popUpDialog() {
    println("showing dialog")
    window.showAll()
    window.present()
  }

  def hideAndRemoveWindow(uniq: Application) {
    window.hide()
    uniq.removeWindow(window)
  }





  // ------------- [ private methods ] -------------------

  private def buildWindow: Window = {
    
    
    
    
    val mainVBox = new VBox(false, 0)

    val menu = new MainMenu

    mainVBox.packStart(menu.widget, false, false, 3)

    val mainHBox = new HBox(false, 0)
    val nav = new NavigationPane
    val testLabel = new Label("test 2") /* HACK */

    val mainHPane = new HPaned(nav.widget, testLabel)
    mainHPane.setPosition(150)
    mainVBox.add(mainHPane)

    // mainVbox add menubar

    ////val mainMenu = new MainMenu()
    ////mainVBox.packStart(mainMenu.menuBar, false, false, 3);

    // mainVbox add toolbar
    ////val mainHPane = new HPaned(navigationPane.widget, viewPane.widget)
    ////mainHPane.setPosition(150)
    ////mainVBox.add(mainHPane)
    // mainVBox add statusbar

    val mainWin = new Window
    mainWin.hide()
    mainWin.setTitle("TomatoJuice")
    //// mainWin.setIcon(Icons.PalatinaIcon)

    //println(org.freedesktop.icons.Helper.getName(org.freedesktop.icons.ApplicationIcon.ACCESSORIES_CALCULATOR ))

    mainWin.add(mainVBox)
    //mainWin.setDefaultSize(1200, 800)
    mainWin.setDefaultSize(800, 600)
    connectWindowDeleteEvent(mainWin)
    //mainWin.showAll
    //// mainWin.setMaximize(true) // just experimenting
    mainWin
  }

  private def connectWindowDeleteEvent(w: Window) {
    implicit val implicitSender = mainWindowActor

    w.connect(new Window.DeleteEvent() {
      def onDeleteEvent(source: Widget, event: Event): Boolean = {
        println("got Window.DeleteEvent")
        mainWindowActor ! "HACK MainWindowActor.CloseUI"
        false
      }
    })
  }

}
