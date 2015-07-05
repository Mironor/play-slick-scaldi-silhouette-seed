package fixtures

import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.impl.providers.{OAuth2Info, OAuth1Info}
import daos.DBTableDefinitions._
import slick.driver.PostgresDriver.api._
import slick.lifted.TableQuery

import scala.concurrent.Future

object SilhouetteFixture {

  lazy val database = Database.forConfig("slick.dbs.default.db")

  val slickOAuth1Infos = TableQuery[OAuth1Infos]
  val slickOAuth2Infos = TableQuery[OAuth2Infos]
  val slickPasswordInfos = TableQuery[PasswordInfos]

  val testUserDBOAuth1Info = DBOAuth1Info(UserFixture.testUserLoginInfoId, "token", "secret")
  val testUserOAuth1Info = OAuth1Info("token", "secret")

  val testUserDBOAuth2Info = DBOAuth2Info(UserFixture.testUserLoginInfoId, "accessToken", Some("tokenType"), Some(5000), Some("refreshToken"))
  val testUserOAuth2Info = OAuth2Info( "accessToken", Some("tokenType"), Some(5000), Some("refreshToken"))

  val testUserDBPasswordInfo = DBPasswordInfo(UserFixture.testUserLoginInfoId, "hasher", "password", Some("salt"))
  val testUserPasswordInfo = PasswordInfo("hasher", "password", Some("salt"))


  def initFixture(): Future[_] = database.run {
    DBIO.seq(
      slickOAuth1Infos += testUserDBOAuth1Info,
      slickOAuth2Infos += testUserDBOAuth2Info,
      slickPasswordInfos += testUserDBPasswordInfo
    )
  }
}
