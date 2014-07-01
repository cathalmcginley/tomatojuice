package org.gnostai.tomatojuice.gtkui

import org.gnome.gdk

class PomodoroIcons(baseDir: String) {

  private def iconNamesFrom(min: Int, max: Int) = {
    for (index <- min to max) yield f"$index%02d.png"
  }
  
  /* @glib_unsafe */
  private def iconsFrom(min: Int, max: Int, baseDir: String) = {
    println(max + "  " + baseDir)
    val icons = iconNamesFrom(min, max).map(name => new gdk.Pixbuf(baseDir + "/" + name))
    println("done")
    icons
  }

  private val pomodoroNotificationDir = baseDir + "/" + "pomodoro-red"
  private val breakNotificationDir = baseDir + "/" + "break-green"

  /* @glib_unsafe */
  val pomodoroIcons = iconsFrom(0, 25, pomodoroNotificationDir)

  /* @glib_unsafe */
  val breakIcons = iconsFrom(0, 5, breakNotificationDir)

  val initial = pomodoroIcons(0)

}