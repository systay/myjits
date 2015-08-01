package models.daos

import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.impl.daos.DelegableAuthInfoDAO
import models.services.DbConnection
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

/**
 * The DAO to store the password information.
 */
class PasswordInfoDAO @Inject()(db: DbConnection) extends DelegableAuthInfoDAO[PasswordInfo] {

  def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = Future {
    val result = db.engine.execute(
      Queries.Password.find, Map("providerID" -> loginInfo.providerID, "providerKey" -> loginInfo.providerKey))

    if (result.isEmpty)
      None
    else {
      val row = result.next()
      val hasher = row("p.hasher").toString
      val password = row("p.password").toString

      Some(PasswordInfo(hasher, password, None))
    }
  }

  def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = Future {
    db.engine.execute(
      Queries.Password.create, createParamsMap(loginInfo, authInfo))

    authInfo
  }

  def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = Future {
    db.engine.execute(
      Queries.Password.update, createParamsMap(loginInfo, authInfo))

    authInfo
  }

  def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    find(loginInfo).flatMap {
      case Some(_) => update(loginInfo, authInfo)
      case None => add(loginInfo, authInfo)
    }
  }

  def remove(loginInfo: LoginInfo): Future[Unit] = Future {
    db.engine.execute(
      Queries.Password.delete, Map("providerID" -> loginInfo.providerID, "providerKey" -> loginInfo.providerKey))
    ()
  }

  private def createParamsMap(loginInfo: LoginInfo, authInfo: PasswordInfo): Map[String, String] = {
    Map(
      "providerID" -> loginInfo.providerID,
      "providerKey" -> loginInfo.providerKey,
      "hasher" -> authInfo.hasher,
      "password" -> authInfo.password
    )
  }
}

