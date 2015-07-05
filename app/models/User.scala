package models

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import daos.DBTableDefinitions.DBUser

/**
 * The user object.
 * Also Identity object for Silhouette
 * @param id The unique ID of the user.
 * @param loginInfo The linked login info.
 * @param email Maybe the email of the authenticated provider.
 * @param avatarURL Maybe the avatar URL of the authenticated provider.
 */
case class User(id: Long,
                loginInfo: LoginInfo,
                email: String,
                avatarURL: Option[String]) extends Identity {

  def toDBUser: DBUser = DBUser(Option(id), email, avatarURL)
}

object User {
  def fromDBUser(dbUser: DBUser, loginInfo: LoginInfo): User = {
    val id = dbUser.id.getOrElse(throw new Exception(
      """A user row in the database did not have
        | an id (id field has autoincrement constraint, so it should not be null).
        | Or you are trying to cast a user's row that does not have id (this is strange)""".stripMargin
    ))

    User(id, loginInfo, dbUser.email, dbUser.avatarURL)
  }
}

