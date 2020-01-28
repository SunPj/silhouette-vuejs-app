package models.services

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
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
    * Saves a user.
    *
    * @param user The user to save.
    * @return The saved user.
    */
  def save(user: User): Future[User]

  /**
    * Saves the social profile for a user.
    *
    * If a user exists for this profile then update the user, otherwise create a new user with the given profile.
    *
    * @param profile The social profile to save.
    * @return The user for whom the profile was saved.
    */
  def save(profile: CommonSocialProfile): Future[User]
}
