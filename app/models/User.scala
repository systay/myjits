package models

import org.mindrot.jbcrypt.BCrypt

object User {
  def createUser(name: String, email: String, password: String) =
    new User(name, email, passwordHash = BCrypt.hashpw(password, BCrypt.gensalt()))
}

case class User(name: String, email: String, passwordHash: String) {
  def checkPassword(plainText: String) = BCrypt.checkpw(plainText, passwordHash)
}