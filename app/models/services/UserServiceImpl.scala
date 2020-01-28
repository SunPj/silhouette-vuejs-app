package models.services

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import javax.inject.Inject
import models.{User, UserRoles}
import models.daos.{LoginInfoDAO, UserDAO}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Handles actions to users.
  *
  * @param userDAO The user DAO implementation.
  */
class UserServiceImpl @Inject()(userDAO: UserDAO,
                                loginInfoDAO: LoginInfoDAO)(implicit ec: ExecutionContext) extends UserService {

  /**
    * Retrieves a user that matches the specified login info.
    *
    * @param loginInfo The login info to retrieve a user.
    * @return The retrieved user or None if no user could be retrieved for the given login info.
    */
  def retrieve(loginInfo: LoginInfo): Future[Option[User]] = userDAO.find(loginInfo)

  /**
    * Saves a user.
    *
    * @param user The user to save.
    * @return The saved user.
    */
  def save(user: User) = userDAO.save(user)

  /**
    * Saves the social profile for a user.
    *
    * If a user exists for this profile then update the user, otherwise create a new user with the given profile.
    *
    * @param profile The social profile to save.
    * @return The user for whom the profile was saved.
    */
  def save(profile: CommonSocialProfile) = {
    userDAO.find(profile.loginInfo).flatMap {
      case Some(user) => // Update user with profile
        userDAO.save(user.copy(
          firstName = profile.firstName,
          lastName = profile.lastName,
          email = profile.email,
          avatarURL = profile.avatarURL
        ))
      case None => // Insert a new user
        userDAO.save(User(
          userID = UUID.randomUUID(),
          firstName = profile.firstName,
          lastName = profile.lastName,
          email = profile.email,
          avatarURL = profile.avatarURL,
          activated = false,
          role = UserRoles.User
        ))
    }
  }

  /**
    * Retrieves a user and login info pair by userID and login info providerID
    *
    * @param id         The ID to retrieve a user.
    * @param providerID The ID of login info provider.
    * @return The retrieved user or None if no user could be retrieved for the given ID.
    */
  def retrieveUserLoginInfo(id: UUID, providerID: String): Future[Option[(User, LoginInfo)]] = {
    loginInfoDAO.find(id, providerID)
  }

  /**
    * Changes role of user
    *
    * @param userId user id
    * @param role   role to assign to user
    * @return
    */
  override def changeUserRole(userId: UUID, role: UserRoles.Value): Future[Boolean] = {
    userDAO.updateUserRole(userId, role)
  }
}
