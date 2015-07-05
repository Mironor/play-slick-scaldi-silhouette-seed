package daos

import com.mohiva.play.silhouette.api.LoginInfo
import daos.silhouette.{SilhouetteDAOException, LoginInfoDAO}
import fixtures.UserFixture
import helpers.SeedSpecification
import org.specs2.matcher.ThrownMessages
import scaldi.Injector

class LoginInfoDAOSpec extends SeedSpecification with ThrownMessages {

  protected def bootstrapFixtures(implicit inj: Injector): Unit = {
    await(UserFixture.initFixture())
  }

  "LoginInfo DAO" should {
    "find login info if it exists" in { implicit inj: Injector =>
      // Given
      val loginInfoDAO = new LoginInfoDAO

      // When
      val foundLoginInfo = await(loginInfoDAO.find(UserFixture.testUserLoginInfo))

      // Then
      foundLoginInfo must beSome
    }

    "find None if login info does not exist" in { implicit inj: Injector =>
      // Given
      val loginInfoDAO = new LoginInfoDAO

      val loginInfo = LoginInfo("some id", "some key")

      // When
      val foundLoginInfo = await(loginInfoDAO.find(loginInfo))

      // Then
      foundLoginInfo must beNone
    }

    "insert new LoginInfo" in { implicit inj: Injector =>
      // Given
      val loginInfoDAO = new LoginInfoDAO

      val newLoginInfo = LoginInfo("some id", "some key")

      // When
      await(loginInfoDAO.insert(newLoginInfo, UserFixture.testUserId))
      val loginInfo = await(loginInfoDAO.find(newLoginInfo))

      // Then
      loginInfo must beSome
    }

    "return LoginInfo's id" in { implicit inj: Injector =>
      // Given
      val loginInfoDAO = new LoginInfoDAO

      // When
      val foundLoginInfo = await(loginInfoDAO.getId(UserFixture.testUserLoginInfo))

      // Then
      foundLoginInfo must beEqualTo(1)
    }

    "throw exception when trying to get LoginInfo's id and it doesn't exist" in { implicit inj: Injector =>
      // Given
      val loginInfoDAO = new LoginInfoDAO

      // When Then
      await(loginInfoDAO.getId(LoginInfo("SomeProvider", "SomeKey"))) must throwA[SilhouetteDAOException]
    }
  }

}
