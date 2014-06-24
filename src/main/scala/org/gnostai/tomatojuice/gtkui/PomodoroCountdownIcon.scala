package org.gnostai.tomatojuice.gtkui

import akka.actor.ActorRef
import org.gnome.gtk.StatusIcon
import org.gnome.gdk.Pixbuf
import org.gnostai.tomatojuice.ui.MainWindowActor


import javax.sound.sampled.AudioSystem
import java.net.URL
import javax.sound.sampled.DataLine
import javax.sound.sampled.Clip

class PomodoroCountdownIcon(mainWindowActor: ActorRef) extends GtkUIFacade {
  private val countdownImages = Seq("00.png",
    "01.png", "02.png", "03.png", "04.png", "05.png",
    "06.png", "07.png", "08.png", "09.png", "10.png",
    "11.png", "12.png", "13.png", "14.png", "15.png",
    "16.png", "17.png", "18.png", "19.png", "20.png",
    "21.png", "22.png", "23.png", "24.png", "25.png")

  private val pomodoroNotificationDir = "/home/cathal/Pictures/pomodoro-tomatojuice/smaller"
  private val breakNotificationDir = "/home/cathal/Pictures/pomodoro-tomatojuice/green/smaller"

  private val pomodoroIcons = countdownImages.map(n => new Pixbuf(pomodoroNotificationDir + "/" + n))
  private val breakIcons = countdownImages.take(6).map(n => new Pixbuf(breakNotificationDir + "/" + n))

  val status = buildStatusIcon

  
  def showPomodoroMinutesRemaining(remaining: Int) {
    status.setFromPixbuf(pomodoroIcons(remaining))
  }
  
  def showBreakMinutesRemaining(remaining: Int) {
    status.setFromPixbuf(breakIcons(remaining))
  }
  
  def hideIcon() {
    status.setVisible(false)
  }
  
    def playSound() {

    val wav = this.getClass.getResource("/complete.wav")
    println(wav)
    val wav2 = new URL("file:///usr/share/sounds/pop.wav")
    val inputStream = AudioSystem.getAudioInputStream(wav)
    val format = inputStream.getFormat
    val info = new DataLine.Info(classOf[Clip], format)
    val clip = AudioSystem.getLine(info).asInstanceOf[Clip]
    clip.open(inputStream)
    clip.start()
  }
  
  private def buildStatusIcon() = {
    val sIcon = new StatusIcon(pomodoroIcons(0))

    sIcon.setVisible(true)
    sIcon.connect(new StatusIcon.Activate() {
      def onActivate(icon: StatusIcon) {
        println("activate")
        mainWindowActor ! MainWindowActor.ActivateStatusIcon
      }
    })
    sIcon
  }

}