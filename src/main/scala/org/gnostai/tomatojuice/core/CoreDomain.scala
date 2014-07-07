package org.gnostai.tomatojuice.core

trait CoreDomainModule {

  sealed trait CountdownTimer
  case object PomodoroCountdownTimer extends CountdownTimer {
    override def toString = "Pomodoro timer"
  }
  case object ShortBreakCountdownTimer extends CountdownTimer {
    override def toString = "short break"
  }
  case object LongBreakCountdownTimer extends CountdownTimer {
    override def toString = "extended break"
  }

  type ImageData = Array[Byte]

  case class Project(id: Option[Int],
    name: String,
    description: String,
    icon: Option[ImageData]) {

  }

}

object CoreDomainModule extends CoreDomainModule {
  
}