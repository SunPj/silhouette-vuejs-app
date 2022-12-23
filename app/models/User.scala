package models

import java.util.UUID

import io.github.honeycombcheesecake.play.silhouette.api.Identity

/**
  * The user object.
  *
  * @param userID    The unique ID of the user.
  * @param firstName Maybe the first name of the authenticated user.
  * @param lastName  Maybe the last name of the authenticated user.
  * @param email     Maybe the email of the authenticated provider.
  * @param avatarURL Maybe the avatar URL of the authenticated provider.
  * @param activated Indicates that the user has activated its registration.
  * @param role      user role
  */
case class User(userID: UUID,
                firstName: Option[String],
                lastName: Option[String],
                email: Option[String],
                avatarURL: Option[String],
                activated: Boolean,
                role: UserRoles.UserRole) extends Identity
