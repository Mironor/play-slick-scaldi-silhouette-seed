package daos

import slick.driver.PostgresDriver.api._
import slick.lifted.{TableQuery, Tag}
import slick.model.ForeignKeyAction

/**
 * Tables definitions for slick
 */
object DBTableDefinitions {

  case class DBUser(id: Option[Long],
                    email: String,
                    avatarURL: Option[String])

  class Users(tag: Tag) extends Table[DBUser](tag, "users") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def email = column[String]("email")

    def avatarURL = column[Option[String]]("avatarURL")

    def * = (id.?, email, avatarURL) <>(DBUser.tupled, DBUser.unapply)
  }

  case class DBLoginInfo(id: Option[Long],
                         idUser: Long,
                         providerID: String,
                         providerKey: String)

  class LoginInfos(tag: Tag) extends Table[DBLoginInfo](tag, "logininfos") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def idUser = column[Long]("idUser")

    def providerID = column[String]("providerID")

    def providerKey = column[String]("providerKey")

    def userFK = foreignKey("LOGININFO_USER_FK", idUser, TableQuery[Users])(_.id, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)

    def * = (id.?, idUser, providerID, providerKey) <>(DBLoginInfo.tupled, DBLoginInfo.unapply)
  }

  case class DBOAuth1Info(idLoginInfo: Long,
                          token: String,
                          secret: String)

  class OAuth1Infos(tag: Tag) extends Table[DBOAuth1Info](tag, "oauth1infos") {
    def idLoginInfo = column[Long]("idLoginInfo", O.PrimaryKey)

    def token = column[String]("token")

    def secret = column[String]("secret")

    def loginInfoFK = foreignKey("OAUTH1INFO_LOGININFO_FK", idLoginInfo, TableQuery[LoginInfos])(_.id, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)

    def * = (idLoginInfo, token, secret) <>(DBOAuth1Info.tupled, DBOAuth1Info.unapply)
  }

  case class DBOAuth2Info(idLoginInfo: Long,
                          accessToken: String,
                          tokenType: Option[String],
                          expiresIn: Option[Int],
                          refreshToken: Option[String])

  class OAuth2Infos(tag: Tag) extends Table[DBOAuth2Info](tag, "oauth2infos") {
    def idLoginInfo = column[Long]("idLoginInfo", O.PrimaryKey)

    def accessToken = column[String]("accesstoken")

    def tokenType = column[Option[String]]("tokentype")

    def expiresIn = column[Option[Int]]("expiresin")

    def refreshToken = column[Option[String]]("refreshtoken")

    def loginInfoFK = foreignKey("OAUTH2INFO_LOGININFO_FK", idLoginInfo, TableQuery[LoginInfos])(_.id, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)

    def * = (idLoginInfo, accessToken, tokenType, expiresIn, refreshToken) <>(DBOAuth2Info.tupled, DBOAuth2Info.unapply)
  }

  case class DBPasswordInfo(idLoginInfo: Long,
                            hasher: String,
                            password: String,
                            salt: Option[String])

  class PasswordInfos(tag: Tag) extends Table[DBPasswordInfo](tag, "passwordinfos") {
    def idLoginInfo = column[Long]("idLoginInfo", O.PrimaryKey)

    def hasher = column[String]("hasher")

    def password = column[String]("password")

    def salt = column[Option[String]]("salt")

    def loginInfoFK = foreignKey("PASSWORDINFO_LOGININFO_FK", idLoginInfo, TableQuery[LoginInfos])(_.id, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)

    def * = (idLoginInfo, hasher, password, salt) <>(DBPasswordInfo.tupled, DBPasswordInfo.unapply)
  }
}
