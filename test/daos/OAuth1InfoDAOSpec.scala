package daos

import com.mohiva.play.silhouette.impl.providers.OAuth1Info
import daos.silhouette.OAuth1InfoDAO
import fixtures.{SilhouetteFixture, UserFixture}
import helpers.SeedSpecification
import org.specs2.matcher.ThrownMessages
import scaldi.Injector

class OAuth1InfoDAOSpec extends SeedSpecification with ThrownMessages {

  protected def bootstrapFixtures(implicit inj: Injector): Unit = {
    await(UserFixture.initFixture())
    await(SilhouetteFixture.initFixture())
  }

  "OAuth1Info DAO" should {
    "find auth info if it exists" in { implicit inj: Injector =>
      // Given
      val auth1InfoDAO = new OAuth1InfoDAO

      // When
      val foundAuth1Info = await(auth1InfoDAO.find(UserFixture.testUserLoginInfo))

      // Then
      foundAuth1Info must beSome(SilhouetteFixture.testUserOAuth1Info)
    }

    "find None if auth info does not exist" in { implicit inj: Injector =>
      // Given
      val auth1InfoDAO = new OAuth1InfoDAO

      // When
      val foundAuth1Info = await(auth1InfoDAO.find(UserFixture.otherUserLoginInfo))

      // Then
      foundAuth1Info must beNone
    }

    "add new auth info" in { implicit inj: Injector =>
      // Given
      val auth1InfoDAO = new OAuth1InfoDAO

      val newAuth1Info = OAuth1Info("SomeToken", "SomeSecret")

      // When
      await(auth1InfoDAO.add(UserFixture.otherUserLoginInfo, newAuth1Info))
      val auth1Info = await(auth1InfoDAO.find(UserFixture.otherUserLoginInfo))

      // Then
      auth1Info must beSome(newAuth1Info)
    }

    "update auth info" in { implicit inj: Injector =>
      // Given
      val auth1InfoDAO = new OAuth1InfoDAO
      val updatedAuthInfo = SilhouetteFixture.testUserOAuth1Info.copy(token = "new token")

      // When
      await(auth1InfoDAO.update(UserFixture.testUserLoginInfo, updatedAuthInfo ))
      val auth1Info = await(auth1InfoDAO.find(UserFixture.testUserLoginInfo))

      // Then
      auth1Info must beSome(updatedAuthInfo)
    }

    "remove auth info" in { implicit inj: Injector =>
      // Given
      val auth1InfoDAO = new OAuth1InfoDAO

      // When
      await(auth1InfoDAO.remove(UserFixture.testUserLoginInfo))
      val auth1Info = await(auth1InfoDAO.find(UserFixture.testUserLoginInfo))

      // Then
      auth1Info must beNone
    }

    "save (insert) auth info if it's new" in { implicit inj: Injector =>
      // Given
      val auth1InfoDAO = new OAuth1InfoDAO

      val newAuth1Info = OAuth1Info("SomeToken", "SomeSecret")

      // When
      await(auth1InfoDAO.save(UserFixture.otherUserLoginInfo, newAuth1Info))
      val auth1Info = await(auth1InfoDAO.find(UserFixture.otherUserLoginInfo))

      // Then
      auth1Info must beSome(newAuth1Info)
    }

    "save (update) auth info if it's NOT new" in { implicit inj: Injector =>
      // Given
      val auth1InfoDAO = new OAuth1InfoDAO
      val updatedAuthInfo = SilhouetteFixture.testUserOAuth1Info.copy(token = "new token")

      // When
      await(auth1InfoDAO.save(UserFixture.testUserLoginInfo, updatedAuthInfo ))
      val auth1Info = await(auth1InfoDAO.find(UserFixture.testUserLoginInfo))

      // Then
      auth1Info must beSome(updatedAuthInfo)
    }
  }
}
