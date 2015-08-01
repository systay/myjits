package models.daos

import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.impl.daos.DelegableAuthInfoDAO
import models.services.DbConnection
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

/**
 * The DAO to store the password information.
 */
class PasswordInfoDAO @Inject()(db: DbConnection) extends DelegableAuthInfoDAO[PasswordInfo] {

  /**
   * Finds the auth info which is linked with the specified login info.
   *
   * @param loginInfo The linked login info.
   * @return The retrieved auth info or None if no auth info could be retrieved for the given login info.
   */
  def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = Future.successful {
    val result = db.engine.execute(
      Queries.Password.find, Map("providerID" -> loginInfo.providerID, "providerKey" -> loginInfo.providerKey))

    if (result.isEmpty)
      None
    else {
      val row = result.next()
      val hasher = row("p.hasher").toString
      val password = row("p.password").toString
      Logger.info(s"password from db: $password")
      Logger.info(s"hasher from db: $hasher")
      Some(PasswordInfo(hasher, password, None))
    }
  }

  /**
   * Adds new auth info for the given login info.
   *
   * @param loginInfo The login info for which the auth info should be added.
   * @param authInfo The auth info to add.
   * @return The added auth info.
   */
  def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    db.engine.execute(
      Queries.Password.create, Map(
        "providerID" -> loginInfo.providerID,
        "providerKey" -> loginInfo.providerKey,
        "hasher" -> authInfo.hasher,
        "password" -> authInfo.password
      ))
    Logger.info(s"password to db: ${authInfo.password}")
    Logger.info(s"hasher to db: ${authInfo.hasher}")

    Future.successful(authInfo)
  }

  /**
   * Updates the auth info for the given login info.
   *
   * @param loginInfo The login info for which the auth info should be updated.
   * @param authInfo The auth info to update.
   * @return The updated auth info.
   */
  def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    db.engine.execute(
      Queries.Password.update, Map(
        "providerID" -> loginInfo.providerID,
        "providerKey" -> loginInfo.providerKey,
        "hasher" -> authInfo.hasher,
        "password" -> authInfo.password
      ))
    Future.successful(authInfo)
  }

  /**
   * Saves the auth info for the given login info.
   *
   * This method either adds the auth info if it doesn't exists or it updates the auth info
   * if it already exists.
   *
   * @param loginInfo The login info for which the auth info should be saved.
   * @param authInfo The auth info to save.
   * @return The saved auth info.
   */
  def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    find(loginInfo).flatMap {
      case Some(_) => update(loginInfo, authInfo)
      case None => add(loginInfo, authInfo)
    }
  }

  /**
   * Removes the auth info for the given login info.
   *
   * @param loginInfo The login info for which the auth info should be removed.
   * @return A future to wait for the process to be completed.
   */
  def remove(loginInfo: LoginInfo): Future[Unit] = {
    db.engine.execute(
      Queries.Password.delete, Map("providerID" -> loginInfo.providerID, "providerKey" -> loginInfo.providerKey))
    Future.successful(())
  }
}

