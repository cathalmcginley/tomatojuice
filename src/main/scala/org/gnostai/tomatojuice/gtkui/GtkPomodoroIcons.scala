package org.gnostai.tomatojuice.gtkui

import org.gnome.gdk

class GtkPomodoroIcons(baseDir: String) {

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

  /* @glib_unsafe */
  private def startIconFrom(baseDir: String) = {
    println(baseDir + "/" + "start.png")
    val x = new gdk.Pixbuf(baseDir + "/" + "start.png")
    println("found " + x)
    x
  }

  private val pomodoroNotificationDir = baseDir + "/" + "pomodoro-red"
  private val breakNotificationDir = baseDir + "/" + "break-green"
  private val longBreakNotificationDir = baseDir + "/" + "long-break-green"

  /* @glib_unsafe */
  val pomodoroIcons = iconsFrom(0, 25, pomodoroNotificationDir)
  val pomodoroStartIcon = startIconFrom(pomodoroNotificationDir)

  /* @glib_unsafe */
  val breakIcons = iconsFrom(0, 5, breakNotificationDir)
  val breakStartIcon = startIconFrom(breakNotificationDir)

  /* @glib_unsafe */
  val longBreakIcons = iconsFrom(0, 25, longBreakNotificationDir)
  val longBreakStartIcon = startIconFrom(longBreakNotificationDir)

  val initial = pomodoroStartIcon

}