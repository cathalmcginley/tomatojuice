package org.gnostai.tomatojuice.actors

import akka.testkit._
import akka.actor._
import org.scalatest._
import org.gnostai.tomatojuice.core._
import com.typesafe.config.ConfigFactory
import java.io.File

trait TestCoreModule extends CoreModule  
  with ProductionCoreConfigurationModule
  with CoreDomainModule
  with CoreMessagesModule

trait TestPomodoroCountdownModule extends PomodoroCountdownModule {
  
  implicit def system: ActorSystem  // satisfied by extending TestKit
  
  
  class TestCountdownActor(val pomodoroTracker: ActorRef) extends Actor with PomodoroCountdownActor {
     
    override def receive = timerInactive
  }
  
  lazy val countdown = TestActorRef(new TestCountdownActor(null))
  
  override def newPomodoroCountdownActor(tracker: ActorRef): Actor = 
    countdown.underlyingActor
  
}  

trait TestPomodoroTrackerModule extends PomodoroTrackerModule {
  class TestPomodoroTrackerActor(val mainApp: ActorRef, val countdownActor: ActorRef) extends Actor with PomodoroTrackerActor {
    
   val pomodorosBeforeLongBreak = config.getInt("tomatojuice.pomodoro.pomodorosBeforeLongBreak")
    
    
    override def receive = timerInactive(PomodoroCountdownTimer, pomodorosBeforeLongBreak)
  }
}
  
// TODO FIX - the config uses the ordinary ~/.config/tomatojuice/application.conf
//            we need to create a PROPER TestCoreConfiguration that loads from src/test/resources

class PomodoroTrackerSpec extends TestKit(ActorSystem("PomodoroTrackerSpec", ConfigFactory.parseFile(new File("src/test/resources/test.conf"))))
  with WordSpecLike
  with MustMatchers
  with BeforeAndAfterAll 
  with PomodoroTrackerModule 
  with TestPomodoroCountdownModule
  with TestPomodoroTrackerModule
  with TestCoreModule {

    import PomodoroTracker._
  

    override def afterAll() { system.shutdown() }

    
      "the pomodoro tracker" should {
        "begin with a 25 timer" in {
          val testTracker = TestActorRef(new TestPomodoroTrackerActor(testActor, testActor))
          testTracker ! TimerActivated
          expectMsg(PomodoroCountdown.StartCountdown(25))
          expectMsg(CoreMessages.NewPomodoroStarted)
         
        }
      }

}