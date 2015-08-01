package models.daos

import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.impl.providers.OAuth2Info
import models.services.DbConnection
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.{JsObject, Json}

import scala.concurrent.Future

/**
 * The DAO to store the OAuth2 information.
 **/
class OAuth2InfoDAO @Inject()(db: DbConnection) extends DelegableAuthInfoDAO[OAuth2Info] {

  def find(loginInfo: LoginInfo): Future[Option[OAuth2Info]] = Future {
    val result = db.engine.execute(
      Queries.OAuth2Info.find, Map("providerID" -> loginInfo.providerID, "providerKey" -> loginInfo.providerKey))

    if (result.isEmpty) {
      Logger.info("No OAuth2 found")
      None
    } else {
      val row = result.next()
      val accessToken = row("p.accessToken").toString
      val tokenType = Option(row("p.tokenType")).map(_.toString)
      val expiresIn = Option(row("p.expiresIn")).map(_.asInstanceOf[Int])
      val refreshToken = Option(row("p.refreshToken")).map(_.toString)
      val params = Option(row("p.params")).map(x => jsonStringToMap(x.toString))
      Logger.info("OAuth2 loaded")
      Some(OAuth2Info(accessToken, tokenType, expiresIn, refreshToken, params))
    }
  }

  def add(loginInfo: LoginInfo, authInfo: OAuth2Info): Future[OAuth2Info] = Future {
    db.engine.execute(
      Queries.OAuth2Info.create, createParamsMap(loginInfo, authInfo))
    Logger.info("OAuth2 added")
    authInfo
  }


  def update(loginInfo: LoginInfo, authInfo: OAuth2Info): Future[OAuth2Info] = Future {
    db.engine.execute(
      Queries.OAuth2Info.update, createParamsMap(loginInfo, authInfo))

    authInfo
  }

  def save(loginInfo: LoginInfo, authInfo: OAuth2Info): Future[OAuth2Info] = {
    find(loginInfo).flatMap {
      case Some(_) => update(loginInfo, authInfo)
      case None => add(loginInfo, authInfo)
    }
  }

  def remove(loginInfo: LoginInfo): Future[Unit] = Future {
    db.engine.execute(
      Queries.OAuth2Info.delete, Map("providerID" -> loginInfo.providerID, "providerKey" -> loginInfo.providerKey))
    Logger.info("OAuth2 deleted")
    ()
  }

  private def jsonStringToMap(input: String): Map[String, String] = Json.parse(input) match {
    case JsObject(map) => map.mapValues(_.toString()).toMap
    case _ => throw new RuntimeException("Expected a Json map but got " + input)
  }

  private def mapToJsonString(input: Map[String, String]): String =
    Json.toJson(input).toString()

  private def createParamsMap(loginInfo: LoginInfo, authInfo: OAuth2Info): Map[String, Any] = {
    Map(
      "providerID" -> loginInfo.providerID,
      "providerKey" -> loginInfo.providerKey,
      "accessToken" -> authInfo.accessToken,
      "tokenType" -> authInfo.tokenType.orNull,
      "expiresIn" -> authInfo.expiresIn.orNull,
      "refreshToken" -> authInfo.refreshToken.orNull,
      "params" -> authInfo.params.map(mapToJsonString).orNull
    )
  }
}