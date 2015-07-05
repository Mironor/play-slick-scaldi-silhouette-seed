package helpers

import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.impl.authenticators.SessionAuthenticator
import daos.DBTableDefinitions._
import models.User
import modules.SpecModule
import org.specs2.execute.AsResult
import org.specs2.specification.ForEach
import play.api.db.DBApi
import play.api.db.evolutions.Evolutions
import play.api.test.PlaySpecification
import scaldi.play.ScaldiApplicationBuilder._
import scaldi.{Injectable, Injector}
import slick.driver.PostgresDriver.api._

abstract class SeedSpecification extends PlaySpecification with Injectable with ForEach[Injector] {
  implicit def silhouetteEnv(implicit inj: Injector) = inject [Environment[User, SessionAuthenticator]]

  lazy val database = Database.forConfig("slick.dbs.default.db")

  val slickUsers = TableQuery[Users]
  val slickLoginInfos = TableQuery[LoginInfos]
  val slickOAuth1Infos = TableQuery[OAuth1Infos]
  val slickOAuth2Infos = TableQuery[OAuth2Infos]
  val slickPasswordInfos = TableQuery[PasswordInfos]

  override protected def foreach[R : AsResult](f: Injector => R) =
    withScaldiInj(modules = Seq(new SpecModule)) { implicit injector =>
      bootstrapFixtures(injector)

      try AsResult(f(injector))
      finally Evolutions.cleanupEvolutions(inject[DBApi].database("default"))
    }

  protected def bootstrapFixtures(implicit inj: Injector): Unit
}
