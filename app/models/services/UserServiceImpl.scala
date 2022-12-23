package models.services

import java.util.UUID

import io.github.honeycombcheesecake.play.silhouette.api.LoginInfo
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

  /**
    * Creates or updates user
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
  override def createOrUpdate(loginInfo: LoginInfo,
                              email: String,
                              firstName: Option[String],
                              lastName: Option[String],
                              avatarURL: Option[String]): Future[User] = {

    Future.sequence(Seq(userDAO.find(loginInfo), userDAO.findByEmail(email))).flatMap { users =>
      users.flatten.headOption match {
        case Some(user) =>
          userDAO.save(user.copy(
            firstName = firstName,
            lastName = lastName,
            email = Some(email),
            avatarURL = avatarURL
          ))
        case None =>
          userDAO.save(User(
            userID = UUID.randomUUID(),
            firstName = firstName,
            lastName = lastName,
            email = Some(email),
            avatarURL = avatarURL,
            activated = false,
            role = UserRoles.User
          ))
      }
    }
  }

  /**
    * Marks user email as activated
    *
    * @param user user
    * @return
    */
  override def setEmailActivated(user: User): Future[User] = {
    userDAO.save(user.copy(activated = true))
  }
}
