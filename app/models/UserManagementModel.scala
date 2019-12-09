package models

import java.time.ZonedDateTime
import java.util.UUID

import play.api.libs.json.Json

case class UserManagementModel(id: UUID,
                               firstName: Option[String],
                               lastName: Option[String],
                               email: Option[String],
                               roleId: Int,
                               confirmed: Boolean,
                               signedUpAt: ZonedDateTime,
                               credentialsProvider: Boolean,
                               googleProvider: Boolean,
                               facebookProvider: Boolean,
                               twitterProvider: Boolean)

object UserManagementModel {
  implicit val w = Json.writes[UserManagementModel]
}