package fixtures

import com.mohiva.play.silhouette.api.LoginInfo
import daos.DBTableDefinitions.{DBLoginInfo, LoginInfos, Users}
import models.User
import slick.driver.PostgresDriver.api._
import slick.lifted.TableQuery

import scala.concurrent.Future

object UserFixture{

  lazy val database = Database.forConfig("slick.dbs.default.db")

  val slickUsers = TableQuery[Users]
  val slickLoginInfos = TableQuery[LoginInfos]

  val testUserId = 1
  val testUserLoginInfoId = 1
  val testUserLoginInfo = LoginInfo("key", "value")
  val testUser = User(testUserId, testUserLoginInfo, "test@test.test", Option("someUrl"))

  val otherUserId = 2
  val otherUserLoginInfoId = 2
  val otherUserLoginInfo = LoginInfo("key_other", "value_other")
  val otherUser = User(otherUserId, otherUserLoginInfo, "test@test.test", None)

  def initFixture(): Future[_] = {
    database.run {
      DBIO.seq(
        slickUsers ++= Seq(testUser.toDBUser, otherUser.toDBUser) ,
        slickLoginInfos ++= Seq(
          DBLoginInfo(Some(testUserLoginInfoId), testUserId, testUserLoginInfo.providerID, testUserLoginInfo.providerKey),
          DBLoginInfo(Some(otherUserLoginInfoId), otherUserId, otherUserLoginInfo.providerID, otherUserLoginInfo.providerKey)
        )
      )
    }
  }
}
