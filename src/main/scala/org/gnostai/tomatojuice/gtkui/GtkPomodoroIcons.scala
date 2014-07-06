package org.gnostai.tomatojuice.gtkui

import org.gnome.gdk
import java.io.File

/* @glib_unsafe */
class GtkPomodoroIcons(baseDir: File) {


  private val pomodoroNotificationDir = new File(baseDir, "pomodoro-red")
  private val breakNotificationDir = new File(baseDir, "break-green")
  private val longBreakNotificationDir = new File(baseDir, "long-break-green")

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

  /* @glib_unsafe */
  private implicit def fileToGdkPixbuf(imageFile: File): gdk.Pixbuf = {
    if (imageFile.exists()) {
     new gdk.Pixbuf(imageFile.getAbsolutePath())
    } else {
      notFoundIcon
    }
  }
  
  private def imageFile(baseDir: File, fileName: String): File = {
    new File(baseDir, fileName)
  }
  
    /* @glib_unsafe */
  private def startIconFrom(baseDir: File): gdk.Pixbuf = {
    new File(baseDir, "start.png")    
  }

  private def iconNamesFrom(min: Int, max: Int) = {
    for (index <- min to max) yield f"$index%02d.png"
  }

  /* @glib_unsafe */
  private def iconsFrom(min: Int, max: Int, baseDir: File): Seq[gdk.Pixbuf] = {
    val toFile = imageFile(baseDir, _: String) 
    val iconsFiles = iconNamesFrom(min, max).map(toFile)
    iconsFiles.map(fileToGdkPixbuf)
  }

  lazy val notFoundIcon: gdk.Pixbuf = {
    // TODO
    null
  }

}