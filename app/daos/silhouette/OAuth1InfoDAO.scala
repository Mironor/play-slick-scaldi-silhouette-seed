package daos.silhouette

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.impl.providers.OAuth1Info
import daos.DBTableDefinitions.{DBOAuth1Info, OAuth1Infos}
import play.api.db.slick._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scaldi.{Injectable, Injector}
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import slick.lifted.TableQuery

import scala.concurrent.Future


class OAuth1InfoDAO(implicit inj: Injector) extends DelegableAuthInfoDAO[OAuth1Info] with HasDatabaseConfig[JdbcProfile] with Injectable {
  val dbConfig = inject[DatabaseConfigProvider].get[JdbcProfile]

  private val slickOAuth1Infos = TableQuery[OAuth1Infos]

  private val loginInfoDao = inject[LoginInfoDAO]

  /**
   * Finds the OAuth1 info which is linked with the specified login info.
   *
   * @param loginInfo The linked login info.
   * @return The retrieved OAuth1 info or None if no OAuth1 info could be retrieved for the given login info.
   */
  override def find(loginInfo: LoginInfo): Future[Option[OAuth1Info]] =
    for {
      dbLoginInfoId <- loginInfoDao.getId(loginInfo)
      dbOAuth1InfoOption <- db.run(slickOAuth1Infos.filter(_.idLoginInfo === dbLoginInfoId).result.headOption)
    } yield dbOAuth1InfoOption.map(dbOAuth1Info => OAuth1Info(dbOAuth1Info.token, dbOAuth1Info.secret))


  /**
   * Saves the OAuth1 info.
   * The code is sub optimal but the readability is more important
   * @param loginInfo The login info for which the auth info should be saved.
   * @param authInfo The OAuth1 info to save.
   * @return The saved OAuth1 info or None if the OAuth1 info couldn't be saved.
   */
  override def save(loginInfo: LoginInfo, authInfo: OAuth1Info): Future[OAuth1Info] =
    find(loginInfo).flatMap{
      case Some(_) => update(loginInfo, authInfo)
      case None => add(loginInfo, authInfo)
    }

  /**
   * Adds auth info to the database
   * @param loginInfo The login info to which the auth info is attached
   * @param authInfo Added auth info
   * @return a future with added authInfo
   */
  override def add(loginInfo: LoginInfo, authInfo: OAuth1Info): Future[OAuth1Info] =
    loginInfoDao.getId(loginInfo).flatMap {
      loginInfoId => db.run(slickOAuth1Infos += DBOAuth1Info(loginInfoId, authInfo.token, authInfo.secret))
    }.map(_ => authInfo)


  /**
   * Updates auth info in the database
   * @param loginInfo The login info to which the auth info is attached
   * @param authInfo Updated auth info
   * @return a future with updated auth info
   */
  override def update(loginInfo: LoginInfo, authInfo: OAuth1Info): Future[OAuth1Info] =
    loginInfoDao.getId(loginInfo).flatMap {
      loginInfoId => db.run(slickOAuth1Infos.update(DBOAuth1Info(loginInfoId, authInfo.token, authInfo.secret)))
    }.map(_ => authInfo)

  /**
   * Removes corresponding auth info
   * @param loginInfo The login info to which the auth info is attached
   * @return a future
   */
  override def remove(loginInfo: LoginInfo): Future[Unit] =
    loginInfoDao.getId(loginInfo).flatMap {
      loginInfoId => db.run(slickOAuth1Infos.filter(_.idLoginInfo === loginInfoId).delete)
    }.map(_ => ()) // Should return Unit type

}

