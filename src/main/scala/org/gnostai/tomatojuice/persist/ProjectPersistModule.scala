package org.gnostai.tomatojuice.persist

import akka.actor._
import akka.pattern.pipe
import scala.concurrent.Future
import org.gnostai.tomatojuice.core.CoreDomainModule
import akka.event.LoggingReceive
import org.gnostai.tomatojuice.core.CoreMessagesModule

trait ProjectPersistModule extends CoreDomainModule with CoreMessagesModule {

  type PROJECT_ID

  object ProjectPersist {

    case class CreateNewProject(name: String, description: String, icon: Option[ImageData], origSender: ActorRef)
    case class ProjectCreated(projectId: PROJECT_ID)
    case object Continue

  }

  abstract class ProjectPersistActor extends Actor with ActorLogging with UnboundedStash {

    import ProjectPersist._

    private implicit val dispatcher = context.system.dispatcher

    def receive: Receive = free

    def busy: Receive = {
      case Continue =>
        context.become(free)
        unstashAll()
      case x =>
        stash()
    }

    def free: Receive = LoggingReceive {
      case CreateNewProject(name, description, icon, origSender) =>
        implicit val dispatcher = context.system.dispatcher
        val origSender = sender
        context.become(busy)
        log.info("asyncRecordNewProject")
        asyncRecordNewProject(name, description, icon, origSender) map { _ => Continue } pipeTo self
      case x @ CoreMessages.SendProjectList(origSender) =>
        log.info("sending project list to " + origSender.path)
        val fut = asyncGetAllProjects(origSender)
        fut onSuccess {
          case projects => origSender ! CoreMessages.ProjectList(projects)
        }
    }        
            

    def asyncRecordNewProject(name: String, description: String, icon: Option[ImageData], origSender: ActorRef): Future[PROJECT_ID]

    def asyncGetAllProjects(origSender: ActorRef): Future[Iterable[Project]]

    
  }
}
