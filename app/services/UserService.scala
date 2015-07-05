package services

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import daos.DBTableDefinitions.DBUser
import daos.UserDAO
import daos.silhouette.LoginInfoDAO
import models.User
import play.api.libs.concurrent.Execution.Implicits._
import scaldi.{Injectable, Injector}

import scala.concurrent.Future

class UserService(implicit inj: Injector) extends IdentityService[User] with Injectable {
  val userDAO = inject[UserDAO]
  val loginInfoDAO = inject[LoginInfoDAO]

  /**
   * Retrieves a user that matches the specified login info.
   * @param loginInfo login info to retrieve a user
   * @return the retrieved user (None if the user could not be found)
   */
  def retrieve(loginInfo: LoginInfo): Future[Option[User]] = {
    val dbUserPromise = userDAO.findByLoginInfo(loginInfo)
    dbUserPromise.map(dbUserOption => dbUserOption.map {
      User.fromDBUser(_, loginInfo)
    })
  }

  /**
   * Creates new user from given info
   * @param loginInfo future user's login info
   * @param email future user's email
   * @param avatarURL future user's avatar url (may be None)
   * @return a promise of a created user
   */
  def create(loginInfo: LoginInfo, email: String, avatarURL: Option[String]): Future[User] = {
    val dbUser = DBUser(None, email, avatarURL)

    for {
      dbUser <- userDAO.insert(dbUser)
      user = User.fromDBUser(dbUser, loginInfo)

      _ <- saveLoginInfo(loginInfo, user.id)
    } yield user
  }

  /**
   * Saves (updates) a user (with its login info).
   * @param user The user to save.
   * @return The saved user (None if user was new and he/she was not inserted).
   */
  def save(user: User): Future[User] = {
    saveUser(user).flatMap {
      savedUser => saveLoginInfo(user.loginInfo, user.id).map(_ => savedUser)
    }
  }

  private def saveUser(user: User): Future[User] = {
    userDAO.update(user.toDBUser).map(x => User.fromDBUser(x, user.loginInfo))
  }

  /**
   * Considering that loginInfo should never be modified, "save" is "create if not exist" here
   * @param loginInfo login info to save
   * @param userId id of the user who will own the login info
   * @return
   */
  private def saveLoginInfo(loginInfo: LoginInfo, userId: Long): Future[_] = {
    loginInfoDAO.find(loginInfo).flatMap {
      case Some(_) => Future.successful(loginInfo) // do nothing if loginInfo already exist
      case None => loginInfoDAO.insert(loginInfo, userId)
    }
  }
}
