package daos.silhouette

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.impl.daos.DelegableAuthInfoDAO
import daos.DBTableDefinitions.{DBPasswordInfo, PasswordInfos}
import play.api.db.slick._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scaldi.{Injectable, Injector}
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import slick.lifted.TableQuery

import scala.concurrent.Future


class PasswordInfoDAO(implicit inj: Injector) extends DelegableAuthInfoDAO[PasswordInfo] with HasDatabaseConfig[JdbcProfile] with Injectable {

  val dbConfig = inject[DatabaseConfigProvider].get[JdbcProfile]

  private val slickPasswordInfos = TableQuery[PasswordInfos]

  private val loginInfoDao = inject[LoginInfoDAO]

  /**
   * Finds the password info which is linked with the specified login info.
   *
   * @param loginInfo The linked login info.
   * @return The retrieved password info or None if no password info could be retrieved for the given login info.
   */
  override def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = for {
    dbLoginInfoId <- loginInfoDao.getId(loginInfo)
    dbPasswordInfoOption <- db.run(slickPasswordInfos.filter(_.idLoginInfo === dbLoginInfoId).result.headOption)
  } yield dbPasswordInfoOption.map(dbPasswordInfo => PasswordInfo(dbPasswordInfo.hasher, dbPasswordInfo.password, dbPasswordInfo.salt))

  /**
   * Saves the password info.
   * Though sub optimal, this code is readable
   * @param loginInfo The login info for which the password info should be saved.
   * @param passwordInfo The password info to save.
   * @return The saved password info or None if the password info couldn't be saved.
   */
  override def save(loginInfo: LoginInfo, passwordInfo: PasswordInfo): Future[PasswordInfo] =
    find(loginInfo).flatMap {
      case Some(_) => update(loginInfo, passwordInfo)
      case None => add(loginInfo, passwordInfo)
    }

  /**
   * Adds password info to the database
   * @param loginInfo The login info to which the password info is attached
   * @param passwordInfo Added password info
   * @return a future with added password info
   */
  override def add(loginInfo: LoginInfo, passwordInfo: PasswordInfo): Future[PasswordInfo] =
    loginInfoDao.getId(loginInfo).flatMap {
      loginInfoId => db.run(slickPasswordInfos += DBPasswordInfo(loginInfoId, passwordInfo.hasher, passwordInfo.password, passwordInfo.salt))
    }.map(_ => passwordInfo)

  /**
   * Updates password info in the database
   * @param loginInfo The login info to which the password info is attached
   * @param passwordInfo Updated password info
   * @return a future with updated password info
   */
  override def update(loginInfo: LoginInfo, passwordInfo: PasswordInfo): Future[PasswordInfo] =
    loginInfoDao.getId(loginInfo).flatMap {
      loginInfoId => db.run(slickPasswordInfos.update(DBPasswordInfo(loginInfoId, passwordInfo.hasher, passwordInfo.password, passwordInfo.salt)))
    }.map(_ => passwordInfo)

  /**
   * Removes corresponding password info
   * @param loginInfo The login info to which the password info is attached
   * @return an empty future
   */
  override def remove(loginInfo: LoginInfo): Future[Unit] =
    loginInfoDao.getId(loginInfo).flatMap {
      loginInfoId => db.run(slickPasswordInfos.filter(_.idLoginInfo === loginInfoId).delete)
    }.map(_ => ())
}
