/*
 * Copyright (C) 2014 Cathal Mc Ginley
 *
 * This file is part of TomatoJuice, a Pomodoro timer-tracker for GNOME.
 *
 * TomatoJuice is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * TomatoJuice is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TomatoJuice; see the file COPYING. If not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1301 USA.
*/

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