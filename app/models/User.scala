package models

import java.util.UUID

import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }
import play.api.libs.json.Json

/**
  * The user object.
  *
  * @param userID    The unique ID of the user.
  * @param loginInfo The linked login info.
  * @param firstName Maybe the first name of the authenticated user.
  * @param lastName  Maybe the last name of the authenticated user.
  * @param email     Maybe the email of the authenticated provider.
  * @param avatarURL Maybe the avatar URL of the authenticated provider.
  * @param activated Indicates that the user has activated its registration.
  * @param roleId    The unique ID of the user roles
  */
case class User(userID: UUID,
                loginInfo: LoginInfo,
                firstName: Option[String],
                lastName: Option[String],
                email: Option[String],
                avatarURL: Option[String],
                activated: Boolean,
                roleId: Option[Int]) extends Identity

/**
 * The companion object.
 */
object User {

  /**
   * Converts the [User] object to Json and vice versa.
   */
  implicit val jsonFormat = Json.format[User]
}
