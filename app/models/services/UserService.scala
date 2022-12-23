package models.services

import java.util.UUID

import io.github.honeycombcheesecake.play.silhouette.api.LoginInfo
import io.github.honeycombcheesecake.play.silhouette.api.services.IdentityService
import models.{User, UserRoles}

import scala.concurrent.Future

/**
  * Handles actions to users.
  */
trait UserService extends IdentityService[User] {
  /**
    * Changes role of user
    *
    * @param userId user id
    * @param role   role to assign to user
    * @return
    */
  def changeUserRole(userId: UUID, role: UserRoles.Value): Future[Boolean]

  /**
    * Retrieves a user and login info pair by userID and login info providerID
    *
    * @param id         The ID to retrieve a user.
    * @param providerID The ID of login info provider.
    * @return The retrieved user or None if no user could be retrieved for the given ID.
    */
  def retrieveUserLoginInfo(id: UUID, providerID: String): Future[Option[(User, LoginInfo)]]

  /**
    * Creates or updates a user
    *
    * If a user exists for given login info or email then update the user, otherwise create a new user with the given data
    *
    * @param loginInfo social profile
    * @param email     user email
    * @param firstName first name
    * @param lastName  last name
    * @param avatarURL avatar URL
    * @return
    */
  def createOrUpdate(loginInfo: LoginInfo,
                     email: String,
                     firstName: Option[String],
                     lastName: Option[String],
                     avatarURL: Option[String]): Future[User]

  /**
    * Marks user email as activated
    *
    * @param user user
    * @return
    */
  def setEmailActivated(user: User): Future[User]
}
