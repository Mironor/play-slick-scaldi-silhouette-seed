package controllers

import com.mohiva.play.silhouette.impl.authenticators.SessionAuthenticator
import com.mohiva.play.silhouette.test._
import fixtures.UserFixture
import helpers.SeedSpecification
import org.specs2.matcher.ThrownMessages
import play.api.test._
import scaldi.Injector

class ApplicationSpec extends SeedSpecification with ThrownMessages {

  protected def bootstrapFixtures(implicit inj: Injector): Unit = {
    await(UserFixture.initFixture())
  }

  "Application controller" should {

    "show login page if user is not authenticated" in { implicit inj: Injector =>
      // Given
      // Authenticated with other user than the one which is stored in current environment
      val request = FakeRequest().withAuthenticator[SessionAuthenticator](UserFixture.otherUserLoginInfo)

      val applicationController = new Application

      val expectedHtml = contentAsString(views.html.index(""))

      // When
      val result = applicationController.index()(request)

      // Then
      status(result) mustEqual OK
      contentType(result) must beSome("text/html")
      contentAsString(result) must beEqualTo(expectedHtml)
    }

    "show redirect to Cloud index page if user is authenticated" in { implicit inj: Injector =>
      // Given
      val request = FakeRequest().withAuthenticator[SessionAuthenticator](UserFixture.testUserLoginInfo)

      val applicationController = new Application

      val expectedHtml = contentAsString(views.html.signedIn("test@test.test"))

      // When
      val result = applicationController.index()(request)

      // Then
      status(result) mustEqual OK
      contentType(result) must beSome("text/html")
      contentAsString(result) must beEqualTo(expectedHtml)
    }
  }
}
