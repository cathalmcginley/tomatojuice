package org.gnostai.tomatojuice.core

trait CoreDomainModule {

  sealed trait CountdownTimer
  case object PomodoroCountdownTimer extends CountdownTimer
  case object ShortBreakCountdownTimer extends CountdownTimer
  case object LongBreakCountdownTimer extends CountdownTimer

  type ImageData = Array[Byte]

  case class Project(id: Option[Int],
    name: String,
    description: String,
    icon: Option[ImageData]) {

  }

}

object CoreDomainModule extends CoreDomainModule {
  
}