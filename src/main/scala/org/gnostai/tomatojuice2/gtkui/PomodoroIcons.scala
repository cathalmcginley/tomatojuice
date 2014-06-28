package org.gnostai.tomatojuice2.gtkui

import org.gnome.gdk

class PomodoroIcons(baseDir: String) {

  println("PomodoroIcons")
  
    private val countdownImages = Seq("00.png",
    "01.png", "02.png", "03.png", "04.png", "05.png",
    "06.png", "07.png", "08.png", "09.png", "10.png",
    "11.png", "12.png", "13.png", "14.png", "15.png",
    "16.png", "17.png", "18.png", "19.png", "20.png",
    "21.png", "22.png", "23.png", "24.png", "25.png")

  private val pomodoroNotificationDir = baseDir + "/pomodoro-red"
  private val breakNotificationDir = baseDir + "/break-green"

  println(pomodoroNotificationDir)
  
  val pomodoroIcons = countdownImages.map(n => new gdk.Pixbuf(pomodoroNotificationDir + "/" + n))
  
  println("got pomodoro icons")
  val breakIcons = countdownImages.take(6).map(n => new gdk.Pixbuf(breakNotificationDir + "/" + n))
  
  

  val initial = pomodoroIcons(0)
  
}