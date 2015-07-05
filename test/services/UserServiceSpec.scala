package services

import com.mohiva.play.silhouette.api.LoginInfo
import daos.silhouette.LoginInfoDAO
import fixtures.UserFixture
import helpers.SeedSpecification
import org.specs2.matcher.ThrownMessages
import scaldi.Injector

class UserServiceSpec extends SeedSpecification with ThrownMessages {

  protected def bootstrapFixtures(implicit inj: Injector): Unit = {
    await(UserFixture.initFixture())
  }

  "User service" should {
    "retrieve user by login info" in { implicit inj: Injector =>
      // Given
      val userService = new UserService

      // When
      val user = await(userService.retrieve(UserFixture.testUserLoginInfo)).getOrElse(fail("User was not found"))

      // Then
      user.email must beEqualTo(UserFixture.testUser.email)
      user.avatarURL must beEqualTo(UserFixture.testUser.avatarURL)
    }

    "save a new user" in { implicit inj: Injector =>
      // Given
      val userService = new UserService
      val newEmail = "new email"
      val newAvatarUrl = Option("new avatar url")
      val newLoginInfo = LoginInfo("new key", "new value")

      // When
      await(userService.create(newLoginInfo, newEmail, newAvatarUrl))
      val user = await(userService.retrieve(newLoginInfo)).getOrElse(fail("User was not found"))

      // Then
      user.email must beEqualTo(newEmail)
      user.avatarURL must beEqualTo(newAvatarUrl)
    }

    "not create a new login info if it already exists" in { implicit inj: Injector =>
      // Given
      val userService = new UserService
      val loginInfoDAO = inject[LoginInfoDAO]

      val newEmail = "new email"

      // When
      val loginInfoCountBefore = await(loginInfoDAO.findAll()).length
      await(userService.create(UserFixture.testUserLoginInfo, newEmail, None))
      val loginInfoCountAfter = await(loginInfoDAO.findAll()).length

      // Then
      loginInfoCountAfter must beEqualTo(loginInfoCountBefore)
    }

    "create new login info attached to the user if the login info does not exist" in { implicit inj: Injector =>
      // Given
      val userService = new UserService
      val loginInfoDAO = inject[LoginInfoDAO]

      val newLoginInfo = LoginInfo("new key", "new value")
      val newEmail = "new email"

      // When
      await(userService.create(newLoginInfo, newEmail, None))
      val loginInfo = await(loginInfoDAO.find(newLoginInfo))

      // Then
      loginInfo must beSome
    }

    "save an already created user" in { implicit inj: Injector =>
      // Given
      val userService = new UserService

      val updatedEmail = "updated email"
      val updatedAvatarUrl = Option("updated avatar url")

      // When
      await(userService.save(UserFixture.testUser.copy(email = updatedEmail, avatarURL = updatedAvatarUrl)))
      val user = await(userService.retrieve(UserFixture.testUserLoginInfo)).getOrElse(fail("User was not found"))

      // Then
      user.email must beEqualTo(updatedEmail)
      user.avatarURL must beEqualTo(updatedAvatarUrl)

    }

  }
}
