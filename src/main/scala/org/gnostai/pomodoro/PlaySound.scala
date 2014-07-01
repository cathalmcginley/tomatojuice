package org.gnostai.pomodoro

import akka.actor.ActorRef
import org.gnome.gtk
import org.gnome.gdk
import javax.sound.sampled.AudioSystem
import java.net.URL
import javax.sound.sampled.DataLine
import javax.sound.sampled.Clip
import java.io.File

object PlaySound {

  def playSound() {

//    val wav = this.getClass.getResource("/complete.wav")
//    println(wav)
    val wav2 = new File("/usr/share/sounds/pop.wav")
    val inputStream = AudioSystem.getAudioInputStream(wav2)
    val format = inputStream.getFormat
    val info = new DataLine.Info(classOf[Clip], format)
    val clip = AudioSystem.getLine(info).asInstanceOf[Clip]
    clip.open(inputStream)
    clip.start()
  }

  def main(args: Array[String]): Unit = {
    playSound()
  }
}