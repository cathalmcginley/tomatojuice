package org.gnostai.tomatojuice.db.actors

import akka.actor._
import org.gnostai.tomatojuice.db.QueryToStream
import java.sql.ResultSet
import akka.pattern.pipe
import org.gnostai.tomatojuice.core.CoreDomainModule
import java.sql.Connection
import org.gnostai.tomatojuice.persist.ProjectPersistModule
import scala.concurrent.Future
import java.sql.Statement
import java.io.ByteArrayInputStream


trait ProjectDatabaseActorModule extends ProjectPersistModule with CoreDomainModule {

  case class ProjectDbId(dbId: Int)

  type PROJECT_ID = ProjectDbId

  class ProjectDatabaseActor(conn: Connection) extends ProjectPersistActor {

    private implicit val dispatcher = context.system.dispatcher

    private val insertSql = "INSERT INTO project (name, description, icon_png)" +
      " VALUES (?, ?, ?)"
    lazy val insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)

    override def free: Receive = {
      case ProjectPersist.CreateNewProject(name, description, icon, origSender) =>
        asyncRecordNewProject(name, description, icon, origSender) map { _ => ProjectPersist.Continue } pipeTo self   
    }

    def asyncRecordNewProject(name: String, description: String,
      icon: Option[ImageData], origSender: ActorRef): Future[PROJECT_ID] = Future {

      insertStmt.setString(1, name)
      insertStmt.setString(2, description)
      icon map {
        bytes => insertStmt.setBlob(3, new ByteArrayInputStream(bytes))
      } getOrElse {
        insertStmt.setNull(3, java.sql.Types.BLOB)
      }
      val count = insertStmt.executeUpdate()
      val rslt = insertStmt.getGeneratedKeys()
      if (count == 1) {
        rslt.next()
        val newDbId = rslt.getInt(1)
        ProjectDbId(newDbId)
      } else {
        // TODO fail at this point
        throw new java.sql.SQLException("failed to insert 'project' " + name)
      }

    }

  }

  class ProjectDBUtil extends QueryToStream {

    private def rsltToProject(rslt: ResultSet): Project = {
      val id = rslt.getInt(1)
      val name = rslt.getString(2)
      val descr = rslt.getString(3)
      val icon = rslt.getBlob(4)
      val iconBytes: Option[Array[Byte]] = if (icon == null) {
        None
      } else {

        Some(icon.getBytes(0, 100))
      }
      Project(Some(id), name, descr, iconBytes)
    }
    //def makeProjectStream(r: ResultSet): Stream[Project] = makeStream(rsltToProject)(r)

    val makeProjectStream = makeStream(rsltToProject)(_)

    def loadAll(conn: Connection): Seq[Project] = {
      val stmt = conn.createStatement()
      val rslt = stmt.executeQuery("SELECT id, name, description, icon_png FROM project")

      val valueStream = makeProjectStream(rslt)

      valueStream

    }

  }
}