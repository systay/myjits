package models.daos

object Queries {

  object Password {
    val find =
      """MATCH (l:LoginInfo {providerID: {providerID}, providerKey: {providerKey}})-[:AUTHENTICATION]->(p:Password)
        |RETURN p.hasher, p>.password""".stripMargin

    val create =
      """MERGE (l:LoginInfo {providerID: {providerID}, providerKey: {providerKey}})
        |CREATE (p:Password {hasher: {hasher}, password: {password}})
        |CREATE (l)-[:AUTHENTICATION]->(p)""".stripMargin

    val update =
      """MATCH (l:LoginInfo {providerID: {providerID}, providerKey: {providerKey}})-[:AUTHENTICATION]->(p:Password)
        |SET p.hasher = {hasher}
        |SET p.password = {password}""".stripMargin

    val delete =
      """MATCH (l:LoginInfo {providerID: {providerID}, providerKey: {providerKey}})-[r:AUTHENTICATION]->(p:Password)
        |DELETE r, p""".stripMargin
  }

  object User {
    val findByID =
      """MATCH (l:LoginInfo)-[:IDENTIFIES]->(u:User {userID: {userID})
        |RETURN u.userID, u.firstName, u.lastName, u.fullName, u.email, u.avatarURL, l.providerID, l. providerKey""".stripMargin

    val findByLogin =
      """MATCH (l:LoginInfo {providerID: {providerID}, providerKey: {providerKey}})-[:IDENTIFIES]->(u:User)
        |RETURN u.userID, u.firstName, u.lastName, u.fullName, u.email, u.avatarURL""".stripMargin

    val create =
      """CREATE (l:LoginInfo {providerID: {providerID}, providerKey: {providerKey}}),
        |       (u:User {userID: {userID}, firstName: {firstName}, lastName: {lastName}, fullName: {fullName}, email: {email}, avatarURL: {avatarURL}}),
        |       (l)-[:IDENTIFIES]->(u)""".stripMargin
  }

  object OAuth2Info {
    val find =
      """MATCH (l:LoginInfo {providerID: {providerID}, providerKey: {providerKey}})-[:AUTHENTICATION]->(p:OAuth2)
        |RETURN p.accessToken, p.tokenType, p.expiresIn, p.refreshToken, p.params""".stripMargin

    val create =
      """MERGE (l:LoginInfo {providerID: {providerID}, providerKey: {providerKey}})
        |CREATE (p:OAuth2 {accessToken: {accessToken}, tokenType: {tokenType}, expiresIn: {expiresIn}, refreshToken: {refreshToken}, params: {params}})
        |CREATE (l)-[:AUTHENTICATION]->(p)""".stripMargin

    val update =
      """MATCH (l:LoginInfo {providerID: {providerID}, providerKey: {providerKey}})-[:AUTHENTICATION]->(p:OAuth2)
        |SET p.accessToken = {accessToken}
        |SET p.tokenType = {tokenType}
        |SET p.expiresIn = {expiresIn}
        |SET p.refreshToken = {refreshToken}
        |SET p.params = {params}""".stripMargin

    val delete =
      """MATCH (l:LoginInfo {providerID: {providerID}, providerKey: {providerKey}})-[r:AUTHENTICATION]->(p:OAuth2)
        |DELETE r, p""".stripMargin
  }
}
