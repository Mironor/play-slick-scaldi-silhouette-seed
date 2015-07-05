package daos

import daos.DBTableDefinitions.DBUser
import fixtures.UserFixture
import helpers.SeedSpecification
import org.specs2.matcher.ThrownMessages
import scaldi.Injector

class UserDAOSpec extends SeedSpecification with ThrownMessages {

  protected def bootstrapFixtures(implicit inj: Injector): Unit = {
    await(UserFixture.initFixture())
  }

  "User DAO" should {
    "find user by its id" in { implicit inj: Injector =>
      // Given
      val userDAO = new UserDAO

      // When
      val user = await(userDAO.findById(UserFixture.testUserId)).getOrElse(fail("User cannot be found"))

      // Then
      user.id must beSome(UserFixture.testUser.id)
      user.email must beEqualTo(UserFixture.testUser.email)
      user.avatarURL must beEqualTo(UserFixture.testUser.avatarURL)
    }

    "find user by its Login Info" in { implicit inj: Injector =>
      // Given
      val userDAO = new UserDAO

      // When
      val user = await(userDAO.findByLoginInfo(UserFixture.testUserLoginInfo)).getOrElse(fail("User cannot be found"))

      // Then
      user.id must beSome(UserFixture.testUser.id)
      user.email must beEqualTo(UserFixture.testUser.email)
      user.avatarURL must beEqualTo(UserFixture.testUser.avatarURL)
    }

    "insert new user" in { implicit inj: Injector =>
      // Given
      val userDAO = new UserDAO

      val newEmail = "new email"
      val newAvatarUrl = Option("new url")
      val newUser = DBUser(None, newEmail, newAvatarUrl)

      // When
      val insertedUser = await(userDAO.insert(newUser))
      val insertedUserId = insertedUser.id.getOrElse(fail("Inserted user does not have id"))
      val foundUser = await(userDAO.findById(insertedUserId)).getOrElse(fail("User cannot be found"))

      // Then
      foundUser.email must beEqualTo(newEmail)
      foundUser.avatarURL must beEqualTo(newAvatarUrl)
    }

    "update a user" in { implicit inj: Injector =>
      // Given
      val userDAO = new UserDAO

      val updatedEmail = "updated email"
      val updatedAvatarUrl = Option("updated url")
      val updatedUser = UserFixture.testUser.toDBUser.copy(email = updatedEmail, avatarURL = updatedAvatarUrl)


      // When
      await(userDAO.update(updatedUser))
      val foundUser = await(userDAO.findById(UserFixture.testUserId)).getOrElse(fail("User cannot be found"))

      // Then
      foundUser.email must beEqualTo(updatedEmail)
      foundUser.avatarURL must beEqualTo(updatedAvatarUrl)
    }
  }


}
