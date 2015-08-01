package models.daos

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import models.User
import models.services.DbConnection

import scala.concurrent.Future

/**
 * Give access to the user object.
 */
class UserDAOImpl @Inject()(db: DbConnection) extends UserDAO {

  /**
   * Finds a user by its login info.
   *
   * @param loginInfo The login info of the user to find.
   * @return The found user or None if no user for the given login info could be found.
   */
  def find(loginInfo: LoginInfo) = Future.successful {
    val dbResult = db.engine.execute(
      Queries.User.findByLogin, Map("providerID" -> loginInfo.providerID, "providerKey" -> loginInfo.providerKey))

    if (dbResult.isEmpty)
      None
    else {
      val row = dbResult.next()
      val user = createUser(loginInfo, row)
      Some(user)
    }
  }

  /**
   * Finds a user by its user ID.
   *
   * @param userID The ID of the user to find.
   * @return The found user or None if no user for the given ID could be found.
   */
  def find(userID: UUID) = Future.successful {
    val dbResult = db.engine.execute(
      Queries.User.findByID, Map("userID" -> userID.toString))

    if (dbResult.isEmpty)
      None
    else {
      val row = dbResult.next()
      val providerID = row("l.providerID").toString
      val providerKey = row("l.providerKey").toString

      val loginInfo = LoginInfo(providerID, providerKey)
      val user = createUser(loginInfo, row)
      Some(user)
    }
  }

  private def createUser(loginInfo: LoginInfo, row: Map[String, Any]): User = {
    val userId = UUID.fromString(row("u.userID").toString)
    val firstName = Option(row("u.firstName")).map(_.toString)
    val lastName = Option(row("u.lastName")).map(_.toString)
    val fullName = Option(row("u.fullName")).map(_.toString)
    val email = Option(row("u.email")).map(_.toString)
    val avatarURL = Option(row("u.avatarURL")).map(_.toString)
    val user = User(userId, loginInfo, firstName, lastName, fullName, email, avatarURL)
    user
  }

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def save(user: User) = {
    db.engine.execute(Queries.User.create, Map(
      "providerID" -> user.loginInfo.providerID,
      "providerKey" -> user.loginInfo.providerKey,
      "userID" -> user.userID.toString,
      "firstName" -> user.firstName.orNull,
      "lastName" -> user.lastName.orNull,
      "fullName" -> user.fullName.orNull,
      "email" -> user.email.orNull,
      "avatarURL" -> user.avatarURL.orNull
    ))
    Future.successful(user)
  }
}
