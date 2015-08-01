package models.daos

object Queries {

  object Password {
    val find =
      """MATCH (l:LoginInfo {providerID: {providerID}, providerKey: {providerKey}})-[:PASSWORD]->(p:PasswordInfo)
        |RETURN p.hasher, p>.password""".stripMargin

    val create =
      """MERGE (l:LoginInfo {providerID: {providerID}, providerKey: {providerKey}})
        |CREATE (l)-[:PASSWORD]->(:PasswordInfo {hasher: {hasher}, password: {password}})""".stripMargin

    val update =
      """MATCH (l:LoginInfo {providerID: {providerID}, providerKey: {providerKey}})-[:PASSWORD]->(p:PasswordInfo)
        |SET p.hasher = {hasher}
        |SET p.password = {password}""".stripMargin

    val delete =
      """MATCH (l:LoginInfo {providerID: {providerID}, providerKey: {providerKey}})-[r:PASSWORD]->(p:PasswordInfo)
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

}
