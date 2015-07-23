package models

import org.neo4j.cypher.ExecutionEngine
import org.neo4j.graphdb.GraphDatabaseService
import org.neo4j.graphdb.factory.GraphDatabaseFactory
import play._

object Global extends GlobalSettings {

  val db: GraphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase("/tmp/jitsdb")
  val engine = new ExecutionEngine(db)


  override def onStart(app: Application) {
    Logger.info("Application has started")
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
  }

}
