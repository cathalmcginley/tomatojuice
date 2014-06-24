package org.gnostai.tomatojuice.gtkui

import org.gnome.glib.Glib
import org.gnome.glib.Handler

trait GtkUIFacade {
  def safely(thunk: => scala.Unit) = {
    Glib.idleAdd(new Handler() {
      override def run() = {
        thunk
        false
      }
    })
  }
}
