package models.services

import java.io.File
import javax.inject.{Inject, Singleton}

import org.neo4j.cypher.ExecutionEngine
import org.neo4j.graphdb.factory.GraphDatabaseFactory
import play.api.Logger
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future

@Singleton
class DbConnection @Inject()(lifecycle: ApplicationLifecycle) {
  private val file = new File("db")
  if(!file.exists()) {
    Logger.info("Creating new database!")
  }
  val db = new GraphDatabaseFactory().newEmbeddedDatabase(file)
  val engine = new ExecutionEngine(db)


  lifecycle.addStopHook { () =>
    Logger.info("database cleanly shutdown")
    Future.successful(db.shutdown())
  }
}
