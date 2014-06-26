package org.gnostai.tomatojuice.gtkui

import akka.actor.ActorRef
import org.gnome.gtk._
import org.gnostai.tomatojuice.ui.MainWindowActor
import org.gnome.gdk.Event
import org.gnome.gdk.Pixbuf
import org.gnome.notify.Notification
import org.gnome.notify.Notify
import tomatojuice.JavaSounds


class MainWindow(mainWindowActor: ActorRef) extends GtkUIFacade {

  //  val pixbuf = new Pixbuf("src/main/resources/25-red-leds-mixed.png")
  //  val pixbuf2 = new Pixbuf("src/main/resources/25-red-leds.png")
  //  val status = new StatusIcon(pixbuf)
  //  status.setVisible(true)
  //  status.connect(new StatusIcon.Activate() {
  //    def onActivate(icon: StatusIcon) {
  //      println("interaction...")
  //    }
  //  })

  val window = buildWindow

  val statusIcon = new PomodoroCountdownIcon(mainWindowActor)

  def activateGui() {
    window.present()

  }

  def hideAndRemoveWindow(uniq: Application) {
    window.hide()
    uniq.removeWindow(window)
  }



  def displayNotification() {
    val note = new Notification("foo", "man", "about")
    note.setTimeout(Notification.NOTIFY_EXPIRES_DEFAULT)
    note.show()
    ////playSound()
  }

  //  def displayTimeout(minsRemaining: Int) {
  //    
  //    if (minsRemaining == 8) {
  //      displayNotification()
  //    }
  //    
  //    println(minsRemaining)
  //    status.setTooltipText(minsRemaining + ":00")
  //    if (minsRemaining % 2 == 0) {
  //      status.setFromPixbuf(pixbuf)
  //    } else {
  //      status.setFromPixbuf(pixbuf2)
  //    }
  //  }

  // ------------- [ private methods ] -------------------

  private def buildWindow: Window = {
    val mainVBox = new VBox(false, 0)

    mainVBox.add(new Label("This is a test")) /* HACK */

    
    val mainHBox = new HBox(false, 0)
    val nav = new NavigationPane
    mainHBox.add(nav.widget)
    mainVBox.add(mainHBox)
    
    // mainVbox add menubar

    ////val mainMenu = new MainMenu()
    ////mainVBox.packStart(mainMenu.menuBar, false, false, 3);

    // mainVbox add toolbar
    ////val mainHPane = new HPaned(navigationPane.widget, viewPane.widget)
    ////mainHPane.setPosition(150)
    ////mainVBox.add(mainHPane)
    // mainVBox add statusbar

    val mainWin = new Window
    mainWin.setTitle("TomatoJuice")
    //// mainWin.setIcon(Icons.PalatinaIcon)

    //println(org.freedesktop.icons.Helper.getName(org.freedesktop.icons.ApplicationIcon.ACCESSORIES_CALCULATOR ))

    mainWin.add(mainVBox)
    //mainWin.setDefaultSize(1200, 800)
    mainWin.setDefaultSize(800, 600)
    connectWindowDeleteEvent(mainWin)
    mainWin.showAll
    //// mainWin.setMaximize(true) // just experimenting
    mainWin
  }

  private def connectWindowDeleteEvent(w: Window) {
    implicit val implicitSender = mainWindowActor

    w.connect(new Window.DeleteEvent() {
      def onDeleteEvent(source: Widget, event: Event): Boolean = {
        println("got Window.DeleteEvent")
        mainWindowActor ! MainWindowActor.CloseUI
        false
      }
    })
  }

}
