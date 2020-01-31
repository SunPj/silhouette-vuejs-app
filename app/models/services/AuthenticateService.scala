package models.services

import java.time.Instant
import java.util.UUID

import akka.actor.ActorRef
import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.impl.exceptions.{IdentityNotFoundException, InvalidPasswordException}
import com.mohiva.play.silhouette.impl.providers._
import javax.inject.{Inject, Named}
import models.User
import models.services.BruteForceDefenderActor._

import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout
import com.mohiva.play.silhouette.api.{AuthInfo, LoginInfo}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import models.daos.LoginInfoDAO

import scala.concurrent.{ExecutionContext, Future}

/**
  *
  * @param userService             The user service implementation.
  * @param credentialsProvider     The credentials provider.
  * @param bruteForceDefenderActor actor that tracks failed user signins and decides whether user allowed to sign in
  * @param authInfoRepository      The auth info repository implementation.
  * @param ec                      The execution context.
  */
class AuthenticateService @Inject()(credentialsProvider: CredentialsProvider,
                                    userService: UserService,
                                    authInfoRepository: AuthInfoRepository,
                                    loginInfoDAO: LoginInfoDAO,
                                    socialProviderRegistry: SocialProviderRegistry,
                                    @Named("brute-force-defender") bruteForceDefenderActor: ActorRef)(implicit ec: ExecutionContext) {
  implicit val timeout: Timeout = 5.seconds

  def credentials(email: String, password: String): Future[AuthenticateResult] = {
    (bruteForceDefenderActor ? IsSignInAllowed(email)).flatMap {
      case SignInAllowed(attemptsAllowed) =>
        val credentials = Credentials(email, password)
        credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
          userService.retrieve(loginInfo).map {
            case Some(user) if !user.activated =>
              NonActivatedUserEmail
            case Some(user) =>
              Success(user)
            case None =>
              UserNotFound
          }
        }.recoverWith {
          case _: InvalidPasswordException =>
            // TODO refactor this. Put InvalidCredentials event to Silhouette's EventBus and listen to it in BruteForceDefenderActor
            bruteForceDefenderActor ! RegisterWrongPasswordSignIn(email)
            Future.successful(InvalidPassword(attemptsAllowed))
          case _: IdentityNotFoundException =>
            Future.successful(UserNotFound)
          case e =>
            Future.failed(e)
        }
      case SignInForbidden(nextSignInAllowedAt) =>
        Future.successful(ToManyAuthenticateRequests(nextSignInAllowedAt))
    }
  }

  /**
    * Creates or fetches existing user for given social profile and binds it with given auth info
    *
    * @param provider social authentication provider
    * @param profile  social profile data
    * @param authInfo authentication info
    * @tparam T type of authentication info
    * @return
    *         NoEmailProvided if social profile email is empty
    *         EmailIsBeingUsed if there is existing user with email which eq to given social profile email and user
    *           has no authentication providers for given provider
    *         AccountBind if everything is ok
    */
  def provideUserForSocialAccount[T <: AuthInfo](provider: String, profile: CommonSocialProfile, authInfo: T): Future[UserForSocialAccountResult] = {
    profile.email match {
      case Some(email) =>
        loginInfoDAO.getAuthenticationProviders(email).flatMap { providers =>
          if (providers.contains(provider) || providers.isEmpty) {
            for {
              user <- userService.createOrUpdate(
                profile.loginInfo,
                email,
                profile.firstName,
                profile.lastName,
                profile.avatarURL
              )
              _ <- addAuthenticateMethod(user.userID, profile.loginInfo, authInfo)
            } yield AccountBound(user)
          } else {
            Future.successful(EmailIsBeingUsed(providers))
          }
        }
      case None =>
        Future.successful(NoEmailProvided)
    }
  }

  /**
    * Adds authentication method to user
    *
    * @param userId    user id
    * @param loginInfo login info
    * @param authInfo  auth info
    * @tparam T tyupe of auth info
    * @return
    */
  def addAuthenticateMethod[T <: AuthInfo](userId: UUID, loginInfo: LoginInfo, authInfo: T): Future[Unit] = {
    for {
      _ <- loginInfoDAO.saveUserLoginInfo(userId, loginInfo)
      _ <- authInfoRepository.add(loginInfo, authInfo)
    } yield ()
  }

  /**
    * Checks whether user have authentication method for given provider id
    *
    * @param userId     user id
    * @param providerId authentication provider id
    * @return true if user has authentication method for given provider id, otherwise false
    */
  def userHasAuthenticationMethod(userId: UUID, providerId: String): Future[Boolean] = {
    loginInfoDAO.find(userId, providerId).map(_.nonEmpty)
  }

  /**
    * Get list of providers of user authentication methods
    *
    * @param email user email
    * @return
    */
  def getAuthenticationProviders(email: String): Future[Seq[String]] = loginInfoDAO.getAuthenticationProviders(email)
}

sealed trait AuthenticateResult
case class Success(user: User) extends AuthenticateResult
case class InvalidPassword(attemptsAllowed: Int) extends AuthenticateResult
object NonActivatedUserEmail extends AuthenticateResult
object UserNotFound extends AuthenticateResult
case class ToManyAuthenticateRequests(nextAllowedAttemptTime: Instant) extends AuthenticateResult

sealed trait UserForSocialAccountResult
case object NoEmailProvided extends UserForSocialAccountResult
case class EmailIsBeingUsed(providers: Seq[String]) extends UserForSocialAccountResult
case class AccountBound(user: User) extends UserForSocialAccountResult