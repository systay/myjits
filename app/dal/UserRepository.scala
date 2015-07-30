package dal

import java.io.File
import javax.inject.{Inject, Singleton}

import models.User
import org.neo4j.cypher.ExecutionEngine
import org.neo4j.graphdb.factory.GraphDatabaseFactory
import play.api.Logger
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future

@Singleton
class UserRepository @Inject()(lifecycle: ApplicationLifecycle) {

  val db = new GraphDatabaseFactory().newEmbeddedDatabase(new File("db"))
  val engine = new ExecutionEngine(db)

  lifecycle.addStopHook { () =>
    Logger.info("database cleanly shutdown")
    Future.successful(db.shutdown())
  }

  def saveUser(name: String, email: String, password: String): User = {
    val user = User.createUser(name = name, email = email, password = password)

    engine.execute("CREATE (:User {name: {name}, email: {email}, passwordHash: {pwHash}})",
      Map("name" -> user.name, "email" -> user.email, "pwHash" -> user.passwordHash))
    Logger.info(s"User with name ${user.name} created")
    user
  }

  def load(email: String): Option[User] = {
    val r = engine.execute("MATCH (user:User {email: {email}}) RETURN user.name, user.passwordHash", Map("email" -> email))
    if (r.isEmpty)
      None
    else {
      val row = r.next()
      val name = row("user.name").toString
      val pwHash = row("user.passwordHash").toString
      Some(User(name = name, email = email, passwordHash = pwHash))
    }
  }
}
