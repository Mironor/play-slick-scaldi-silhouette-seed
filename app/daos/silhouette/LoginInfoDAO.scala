package daos.silhouette

import com.mohiva.play.silhouette.api.LoginInfo
import daos.DBTableDefinitions.{DBLoginInfo, LoginInfos}
import daos.SeedDAO
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scaldi.Injector
import slick.driver.PostgresDriver.api._
import slick.lifted.TableQuery

import scala.concurrent.Future

class LoginInfoDAO(implicit inj: Injector) extends SeedDAO{

  private val slickLoginInfos = TableQuery[LoginInfos]

  /**
   * Find supplied login info in the database
   * @return a promise of found login info (None if not found)
   */
  def find(loginInfo: LoginInfo): Future[Option[DBLoginInfo]] = db.run {
    slickLoginInfos.filter(info => info.providerID === loginInfo.providerID && info.providerKey === loginInfo.providerKey)
      .result.headOption
  }

  /**
   * Returns all login infos stored in the database
   * @return a promise of a list of all login infos in the database
   */
  def findAll(): Future[Seq[DBLoginInfo]] = db.run {
    slickLoginInfos.result
  }

  /**
   * Inserts a new LoginInfo in the database
   * @param loginInfo new login info
   * @param userId the user to whom the login info will be attached
   * @return The saved user.
   */
  def insert(loginInfo: LoginInfo, userId: Long): Future[DBLoginInfo] = db.run {
    slickLoginInfos.returning(slickLoginInfos.map(_.id)) += DBLoginInfo(None, userId, loginInfo.providerID, loginInfo.providerKey)
  }.map(id => DBLoginInfo(Option(id), userId, loginInfo.providerID, loginInfo.providerKey))

  /**
   * Finds login info's id (NOT wrapped in option, so it throws exception if login info is not found
   * @param loginInfo the id of this login info will be fetched
   * @return id (Long), throws exception if loginInfo is not found
   */
  def getId(loginInfo: LoginInfo): Future[Long] = db.run {
    slickLoginInfos.filter(info => info.providerID === loginInfo.providerID && info.providerKey === loginInfo.providerKey).map(_.id)
      .result.headOption
  }.map(_.getOrElse(throw new SilhouetteDAOException("Associated LoginInfo not found")))
}
