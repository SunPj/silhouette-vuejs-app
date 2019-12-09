package models.daos

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import models.{User, UserRoles}

import scala.concurrent.Future

/**
 * Give access to the user object.
 */
trait UserDAO {
  /**
    * Updates user role
    *
    * @param userId user id
    * @param role   user role to update to
    * @return
    */
  def updateUserRole(userId: UUID, role: UserRoles.Value): Future[Boolean]

  /**
    * Finds a user by its login info.
    *
    * @param loginInfo The login info of the user to find.
    * @return The found user or None if no user for the given login info could be found.
    */
  def find(loginInfo: LoginInfo): Future[Option[User]]

  /**
    * Finds a user by its user ID.
    *
    * @param userID The ID of the user to find.
    * @return The found user or None if no user for the given ID could be found.
    */
  def find(userID: UUID): Future[Option[User]]

  /**
    * Saves a user.
    *
    * @param user The user to save.
    * @return The saved user.
    */
  def save(user: User): Future[User]
}
