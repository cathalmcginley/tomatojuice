package org.gnostai.pomodoro.gtkui

import akka.actor.ActorRef
import org.gnome.gtk._
import org.gnome.gdk.Event
import org.gnome.gdk.Pixbuf
import org.gnome.notify.Notification
import org.gnome.notify.Notify
import tomatojuice.JavaSounds

// NOT YET RE-IMPLEMENTED

class PomodoroStatusIcon(statusActor: ActorRef) extends GtkUIFacade {

  val statusIcon = new PomodoroCountdownIcon(statusActor)

  def displayNotification() {
    val note = new Notification("foo", "man", "about")
    note.setTimeout(Notification.NOTIFY_EXPIRES_DEFAULT)
    note.addAction("alpha", "Add Note", new Notification.Action() {
      def onAction(source: Notification,
        action: String) {
        println("action string " + action)
      }
    })
    note.show()
    ////playSound()
    
  }

}