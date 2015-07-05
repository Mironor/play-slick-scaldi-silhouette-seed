package daos

import com.mohiva.play.silhouette.impl.providers.OAuth2Info
import daos.silhouette.OAuth2InfoDAO
import fixtures.{SilhouetteFixture, UserFixture}
import helpers.SeedSpecification
import org.specs2.matcher.ThrownMessages
import scaldi.Injector

class OAuth2InfoDAOSpec extends SeedSpecification with ThrownMessages {

  protected def bootstrapFixtures(implicit inj: Injector): Unit = {
    await(UserFixture.initFixture())
    await(SilhouetteFixture.initFixture())
  }

  "OAuth2Info DAO" should {
    "find auth info if it exists" in { implicit inj: Injector =>
      // Given
      val auth2InfoDAO = new OAuth2InfoDAO

      // When
      val foundAuth2Info = await(auth2InfoDAO.find(UserFixture.testUserLoginInfo))

      // Then
      foundAuth2Info must beSome(SilhouetteFixture.testUserOAuth2Info)
    }

    "find None if auth info does not exist" in { implicit inj: Injector =>
      // Given
      val auth2InfoDAO = new OAuth2InfoDAO

      // When
      val foundAuth2Info = await(auth2InfoDAO.find(UserFixture.otherUserLoginInfo))

      // Then
      foundAuth2Info must beNone
    }

    "add new auth info" in { implicit inj: Injector =>
      // Given
      val auth2InfoDAO = new OAuth2InfoDAO

      val newAuth2Info = OAuth2Info("SomeAccesToken", Some("SomeTokenType"), Some(999), Some("SomeRefreshToken"))

      // When
      await(auth2InfoDAO.add(UserFixture.otherUserLoginInfo, newAuth2Info))
      val auth2Info = await(auth2InfoDAO.find(UserFixture.otherUserLoginInfo))

      // Then
      auth2Info must beSome(newAuth2Info)
    }

    "update auth info" in { implicit inj: Injector =>
      // Given
      val auth2InfoDAO = new OAuth2InfoDAO
      val updatedAuthInfo = SilhouetteFixture.testUserOAuth2Info.copy(accessToken = "new token")

      // When
      await(auth2InfoDAO.update(UserFixture.testUserLoginInfo, updatedAuthInfo ))
      val auth2Info = await(auth2InfoDAO.find(UserFixture.testUserLoginInfo))

      // Then
      auth2Info must beSome(updatedAuthInfo)
    }

    "remove auth info" in { implicit inj: Injector =>
      // Given
      val auth2InfoDAO = new OAuth2InfoDAO

      // When
      await(auth2InfoDAO.remove(UserFixture.testUserLoginInfo))
      val auth2Info = await(auth2InfoDAO.find(UserFixture.testUserLoginInfo))

      // Then
      auth2Info must beNone
    }

    "save (insert) auth info if it's new" in { implicit inj: Injector =>
      // Given
      val auth2InfoDAO = new OAuth2InfoDAO

      val newAuth2Info = OAuth2Info("SomeAccesToken", Some("SomeTokenType"), Some(999), Some("SomeRefreshToken"))

      // When
      await(auth2InfoDAO.save(UserFixture.otherUserLoginInfo, newAuth2Info))
      val auth2Info = await(auth2InfoDAO.find(UserFixture.otherUserLoginInfo))

      // Then
      auth2Info must beSome(newAuth2Info)
    }

    "save (update) auth info if it's NOT new" in { implicit inj: Injector =>
      // Given
      val auth2InfoDAO = new OAuth2InfoDAO
      val updatedAuthInfo = SilhouetteFixture.testUserOAuth2Info.copy(accessToken = "new token")

      // When
      await(auth2InfoDAO.save(UserFixture.testUserLoginInfo, updatedAuthInfo ))
      val auth2Info = await(auth2InfoDAO.find(UserFixture.testUserLoginInfo))

      // Then
      auth2Info must beSome(updatedAuthInfo)
    }
  }
}
