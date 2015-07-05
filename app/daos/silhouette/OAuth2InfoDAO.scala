package daos.silhouette

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.impl.providers.OAuth2Info
import daos.DBTableDefinitions.{DBOAuth2Info, OAuth2Infos}
import play.api.db.slick._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scaldi.{Injectable, Injector}
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import slick.lifted.TableQuery

import scala.concurrent.Future


class OAuth2InfoDAO(implicit inj: Injector) extends DelegableAuthInfoDAO[OAuth2Info] with HasDatabaseConfig[JdbcProfile] with Injectable {

  val dbConfig = inject[DatabaseConfigProvider].get[JdbcProfile]

  private val slickOAuth2Infos = TableQuery[OAuth2Infos]

  private val loginInfoDao = inject[LoginInfoDAO]

  /**
   * Finds the OAuth2 info which is linked with the specified login info.
   *
   * @param loginInfo The linked login info.
   * @return The retrieved OAuth2 info or None if no OAuth2 info could be retrieved for the given login info.
   */
  override def find(loginInfo: LoginInfo): Future[Option[OAuth2Info]] = for {
    dbLoginInfoId <- loginInfoDao.getId(loginInfo)
    dbOAuth2InfoOption <- db.run(slickOAuth2Infos.filter(_.idLoginInfo === dbLoginInfoId).result.headOption)
  } yield dbOAuth2InfoOption.map(dbOAuth1Info => OAuth2Info(dbOAuth1Info.accessToken, dbOAuth1Info.tokenType, dbOAuth1Info.expiresIn, dbOAuth1Info.refreshToken))

  /**
   * Saves the OAuth2 info.
   * Though sub optimal, this code is very readable
   * @param loginInfo The login info for which the auth info should be saved.
   * @param authInfo The OAuth2 info to save.
   * @return The saved OAuth2 info or None if the OAuth2 info couldn't be saved.
   */
  override def save(loginInfo: LoginInfo, authInfo: OAuth2Info): Future[OAuth2Info] =
    find(loginInfo).flatMap {
      case Some(_) => update(loginInfo, authInfo)
      case None => add(loginInfo, authInfo)
    }

  /**
   * Adds auth info to the database
   * @param loginInfo The login info to which the auth info is attached
   * @param authInfo Added auth info
   * @return a future with added authInfo
   */
  override def add(loginInfo: LoginInfo, authInfo: OAuth2Info): Future[OAuth2Info] =
    loginInfoDao.getId(loginInfo).flatMap {
      loginInfoId => db.run(slickOAuth2Infos += DBOAuth2Info(loginInfoId, authInfo.accessToken, authInfo.tokenType, authInfo.expiresIn, authInfo.refreshToken))
    }.map(_ => authInfo)

  /**
   * Updates auth info in the database
   * @param loginInfo The login info to which the auth info is attached
   * @param authInfo Updated auth info
   * @return a future with updated auth info
   */
  override def update(loginInfo: LoginInfo, authInfo: OAuth2Info): Future[OAuth2Info] =
    loginInfoDao.getId(loginInfo).flatMap {
      loginInfoId => db.run(slickOAuth2Infos.update(DBOAuth2Info(loginInfoId, authInfo.accessToken, authInfo.tokenType, authInfo.expiresIn, authInfo.refreshToken)))
    }.map(_ => authInfo)

  /**
   * Removes corresponding auth info
   * @param loginInfo The login info to which the auth info is attached
   * @return an empty future
   */
  override def remove(loginInfo: LoginInfo): Future[Unit] =
    loginInfoDao.getId(loginInfo).flatMap {
      loginInfoId => db.run(slickOAuth2Infos.filter(_.idLoginInfo === loginInfoId).delete)
    }.map(_ => ()) // Should return Unit as in the signature
}


