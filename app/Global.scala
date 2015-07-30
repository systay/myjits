import java.io.File

import com.google.inject.{AbstractModule, Guice}
import org.neo4j.cypher.ExecutionEngine
import org.neo4j.graphdb.GraphDatabaseService
import org.neo4j.graphdb.factory.GraphDatabaseFactory
import play.api.{Application, GlobalSettings, Logger}


object Global extends GlobalSettings {

//  var db: GraphDatabaseService = null
//
//  override def onStart(app: Application) {
//    db = new GraphDatabaseFactory().newEmbeddedDatabase(new File(app.path, "db"))
//    val engine = new ExecutionEngine(db)
//
//    Guice.createInjector(new AbstractModule {
//      protected def configure() {
//        bind(classOf[ExecutionEngine]).toInstance(engine)
//      }
//    })
//
//    Logger.info("Application has started")
//  }
//
//
//  override def onStop(app: Application) {
//    db.shutdown()
//    db = null
//    Logger.info("Application shutdown...")
//  }
}
