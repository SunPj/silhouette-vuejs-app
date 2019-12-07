package models.daos

import java.time.ZonedDateTime
import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import models.AuthToken
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape.proveShapeOf

trait DBTableDefinitions {

  protected val profile: JdbcProfile
  import profile.api._

  case class DBUserRole(id: Int, name: String)

  class AuthTokens(tag: Tag) extends Table[AuthToken](tag, Some("auth"), "token") {
    import MyPostgresProfile.api._

    def id = column[UUID]("id", O.PrimaryKey)
    def userId = column[UUID]("user_id")
    def expiry = column[ZonedDateTime]("expiry")
    def * = (id, userId, expiry) <> (AuthToken.tupled, AuthToken.unapply)
  }

  class UserRoles(tag: Tag) extends Table[DBUserRole](tag, Some("auth"), "role") {
    def id = column[Int]("id", O.PrimaryKey)
    def name = column[String]("name")
    def * = (id, name) <> (DBUserRole.tupled, DBUserRole.unapply)
  }

  case class DBUser(userID: UUID,
                    firstName: Option[String],
                    lastName: Option[String],
                    email: Option[String],
                    avatarURL: Option[String],
                    activated: Boolean,
                    roleId: Int,
                    signedUpAt: ZonedDateTime)

  class Users(tag: Tag) extends Table[DBUser](tag, Some("auth"), "user") {
    import MyPostgresProfile.api._

    def id = column[UUID]("id", O.PrimaryKey)
    def firstName = column[Option[String]]("first_name")
    def lastName = column[Option[String]]("last_name")
    def email = column[Option[String]]("email")
    def avatarURL = column[Option[String]]("avatar_url")
    def activated = column[Boolean]("activated")
    def roleId = column[Int]("role_id")
    def signedUpAt = column[ZonedDateTime]("signed_up_at")
    def * = (id, firstName, lastName, email, avatarURL, activated, roleId, signedUpAt) <> (DBUser.tupled, DBUser.unapply)
  }

  case class DBLoginInfo(
    id: Option[Long],
    providerID: String,
    providerKey: String
  )

  class LoginInfos(tag: Tag) extends Table[DBLoginInfo](tag, Some("auth"), "login_info") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def providerID = column[String]("provider_id")
    def providerKey = column[String]("provider_key")
    def * = (id.?, providerID, providerKey) <> (DBLoginInfo.tupled, DBLoginInfo.unapply)
  }

  case class DBUserLoginInfo(
    userID: UUID,
    loginInfoId: Long
  )

  class UserLoginInfos(tag: Tag) extends Table[DBUserLoginInfo](tag, Some("auth"), "user_login_info") {
    def userID = column[UUID]("user_id")
    def loginInfoId = column[Long]("login_info_id")
    def * = (userID, loginInfoId) <> (DBUserLoginInfo.tupled, DBUserLoginInfo.unapply)
  }

  case class DBPasswordInfo(
    hasher: String,
    password: String,
    salt: Option[String],
    loginInfoId: Long
  )

  class PasswordInfos(tag: Tag) extends Table[DBPasswordInfo](tag, Some("auth"), "password_info") {
    def hasher = column[String]("hasher")
    def password = column[String]("password")
    def salt = column[Option[String]]("salt")
    def loginInfoId = column[Long]("login_info_id")
    def * = (hasher, password, salt, loginInfoId) <> (DBPasswordInfo.tupled, DBPasswordInfo.unapply)
  }

  case class DBOAuth1Info(
    id: Option[Long],
    token: String,
    secret: String,
    loginInfoId: Long
  )

  class OAuth1Infos(tag: Tag) extends Table[DBOAuth1Info](tag, Some("auth"), "oauth1_info") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def token = column[String]("token")
    def secret = column[String]("secret")
    def loginInfoId = column[Long]("login_info_id")
    def * = (id.?, token, secret, loginInfoId) <> (DBOAuth1Info.tupled, DBOAuth1Info.unapply)
  }

  case class DBOAuth2Info(
    id: Option[Long],
    accessToken: String,
    tokenType: Option[String],
    expiresIn: Option[Int],
    refreshToken: Option[String],
    loginInfoId: Long
  )

  class OAuth2Infos(tag: Tag) extends Table[DBOAuth2Info](tag, Some("auth"), "oauth2_info") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def accessToken = column[String]("access_token")
    def tokenType = column[Option[String]]("token_type")
    def expiresIn = column[Option[Int]]("expires_in")
    def refreshToken = column[Option[String]]("refresh_token")
    def loginInfoId = column[Long]("login_info_id")
    def * = (id.?, accessToken, tokenType, expiresIn, refreshToken, loginInfoId) <> (DBOAuth2Info.tupled, DBOAuth2Info.unapply)
  }


  // table query definitions
  val slickUsers = TableQuery[Users]
  val slickAuthTokens = TableQuery[AuthTokens]
  val slickUserRoles = TableQuery[UserRoles]
  val slickLoginInfos = TableQuery[LoginInfos]
  val slickUserLoginInfos = TableQuery[UserLoginInfos]
  val slickPasswordInfos = TableQuery[PasswordInfos]
  val slickOAuth1Infos = TableQuery[OAuth1Infos]
  val slickOAuth2Infos = TableQuery[OAuth2Infos]

  // queries used in multiple places
  def loginInfoQuery(loginInfo: LoginInfo) =
    slickLoginInfos.filter(dbLoginInfo => dbLoginInfo.providerID === loginInfo.providerID && dbLoginInfo.providerKey === loginInfo.providerKey)

}
