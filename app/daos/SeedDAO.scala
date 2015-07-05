package daos

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import scaldi.Injectable
import scaldi.Injector
import slick.driver.JdbcProfile

class SeedDAO(implicit inj: Injector) extends HasDatabaseConfig[JdbcProfile] with Injectable {
  // Using the same database connection for every DAO extending this class
  val dbConfig = inject[DatabaseConfigProvider].get[JdbcProfile]
}
