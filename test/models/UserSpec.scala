package models

import com.mohiva.play.silhouette.api.LoginInfo
import daos.DBTableDefinitions.DBUser
import play.api.test.PlaySpecification

class UserSpec extends PlaySpecification{
  "User model" should {
    "be creatable from dbUser" in {
      // Given
      val dbUser = DBUser(Option(1), "email", Option("url"))
      val loginInfo = LoginInfo("id", "key")

      // When
      val user = User.fromDBUser(dbUser, loginInfo)

      // Then
      user.id must beEqualTo(1)
      user.loginInfo must beEqualTo(loginInfo)
      user.email must beEqualTo("email")
      user.avatarURL must beSome("url")
    }

    "throw an exception if created from dbUser that has no id" in {
      // Given
      val dbUser = DBUser(None, "email", Option("url"))
      val loginInfo = LoginInfo("id", "key")

      // When // Then
      User.fromDBUser(dbUser, loginInfo) must throwA[Exception]
    }

    "be castable in dbUser" in {
      // Given
      val loginInfo = LoginInfo("id", "key")
      val user = User(1, loginInfo, "email", Option("url"))

      // When
      val dbUser = user.toDBUser

      // Then
      dbUser.id must beSome(1)
      dbUser.email must beEqualTo("email")
      dbUser.avatarURL must beSome("url")
    }
  }
}
