package daos

import com.mohiva.play.silhouette.api.util.PasswordInfo
import daos.silhouette.PasswordInfoDAO
import fixtures.{SilhouetteFixture, UserFixture}
import helpers.SeedSpecification
import org.specs2.matcher.ThrownMessages
import scaldi.Injector

class PasswordInfoDAOSpec extends SeedSpecification with ThrownMessages {

  protected def bootstrapFixtures(implicit inj: Injector): Unit = {
    await(UserFixture.initFixture())
    await(SilhouetteFixture.initFixture())
  }

  "PasswordInfo DAO" should {
    "find password info if it exists" in { implicit inj: Injector =>
      // Given
      val passwordInfoDAO = new PasswordInfoDAO

      // When
      val foundPasswordInfo = await(passwordInfoDAO.find(UserFixture.testUserLoginInfo))

      // Then
      foundPasswordInfo must beSome(SilhouetteFixture.testUserPasswordInfo)
    }

    "find None if password info does not exist" in { implicit inj: Injector =>
      // Given
      val passwordInfoDAO = new PasswordInfoDAO

      // When
      val foundPasswordInfo = await(passwordInfoDAO.find(UserFixture.otherUserLoginInfo))

      // Then
      foundPasswordInfo must beNone
    }

    "add new password info" in { implicit inj: Injector =>
      // Given
      val passwordInfoDAO = new PasswordInfoDAO

      val newPasswordInfo = PasswordInfo("SomeHasher", "SomePassword", Some("SomeSalt"))

      // When
      await(passwordInfoDAO.add(UserFixture.otherUserLoginInfo, newPasswordInfo))
      val passwordInfo = await(passwordInfoDAO.find(UserFixture.otherUserLoginInfo))

      // Then
      passwordInfo must beSome(newPasswordInfo)
    }

    "update password info" in { implicit inj: Injector =>
      // Given
      val passwordInfoDAO = new PasswordInfoDAO
      val updatedPasswordInfo = SilhouetteFixture.testUserPasswordInfo.copy(hasher = "new token")

      // When
      await(passwordInfoDAO.update(UserFixture.testUserLoginInfo, updatedPasswordInfo ))
      val passwordInfo = await(passwordInfoDAO.find(UserFixture.testUserLoginInfo))

      // Then
      passwordInfo must beSome(updatedPasswordInfo)
    }

    "remove password info" in { implicit inj: Injector =>
      // Given
      val passwordInfoDAO = new PasswordInfoDAO

      // When
      await(passwordInfoDAO.remove(UserFixture.testUserLoginInfo))
      val passwordInfo = await(passwordInfoDAO.find(UserFixture.testUserLoginInfo))

      // Then
      passwordInfo must beNone
    }

    "save (insert) password info if it's new" in { implicit inj: Injector =>
      // Given
      val passwordInfoDAO = new PasswordInfoDAO

      val newPasswordInfo = PasswordInfo("SomeHasher", "SomePassword", Some("SomeSalt"))

      // When
      await(passwordInfoDAO.save(UserFixture.otherUserLoginInfo, newPasswordInfo))
      val passwordInfo = await(passwordInfoDAO.find(UserFixture.otherUserLoginInfo))

      // Then
      passwordInfo must beSome(newPasswordInfo)
    }

    "save (update) password info if it's NOT new" in { implicit inj: Injector =>
      // Given
      val passwordInfoDAO = new PasswordInfoDAO
      val updatedPasswordInfo = SilhouetteFixture.testUserPasswordInfo.copy(hasher = "new token")

      // When
      await(passwordInfoDAO.save(UserFixture.testUserLoginInfo, updatedPasswordInfo ))
      val passwordInfo = await(passwordInfoDAO.find(UserFixture.testUserLoginInfo))

      // Then
      passwordInfo must beSome(updatedPasswordInfo)
    }
  }
}
