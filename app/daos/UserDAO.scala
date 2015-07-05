package daos

import com.mohiva.play.silhouette.api.LoginInfo
import daos.DBTableDefinitions.{DBUser, LoginInfos, Users}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scaldi.Injector
import slick.driver.PostgresDriver.api._
import slick.lifted.TableQuery

import scala.concurrent.Future


class UserDAO(implicit inj: Injector) extends SeedDAO{

  private val slickUsers = TableQuery[Users]
  private val slickLoginInfos = TableQuery[LoginInfos]

  /**
   * Finds a user by its id
   * @param userId user's id
   * @return The found user (None if user could not be found)
   */
  def findById(userId: Long): Future[Option[DBUser]] = db.run {
    slickUsers.filter(_.id === userId).result.headOption
  }

  /**
   * Finds a user by its login info.
   * @param loginInfo The login info of the user to find.
   * @return The found user or None if no user for the given login info could be found.
   */
  def findByLoginInfo(loginInfo: LoginInfo): Future[Option[DBUser]] = db.run {
    (for {
      lInfo <- slickLoginInfos if lInfo.providerID === loginInfo.providerID && lInfo.providerKey === loginInfo.providerKey
      user <- slickUsers if user.id === lInfo.idUser
    } yield user).result.headOption
  }

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def insert(user: DBUser): Future[DBUser] = db.run {
    (slickUsers returning slickUsers.map(_.id)) += user
  }.map(userId => user.copy(id = Option(userId)))

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def update(user: DBUser): Future[DBUser] = db.run {
    slickUsers.filter(_.id === user.id).update(user)
  }.map(_ => user)

}
