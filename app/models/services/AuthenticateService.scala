package models.services

import java.time.Instant

import akka.actor.ActorRef
import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.impl.exceptions.{IdentityNotFoundException, InvalidPasswordException}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import javax.inject.{Inject, Named}
import models.User
import models.services.BruteForceDefenderActor._

import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.{ExecutionContext, Future}

/**
  *
  * @param userService             The user service implementation.
  * @param credentialsProvider     The credentials provider.
  * @param bruteForceDefenderActor actor that tracks failed user signins and decides whether user allowed to sign in
  * @param ec                      The execution context.
  */
class AuthenticateService @Inject()(credentialsProvider: CredentialsProvider,
                                    userService: UserService,
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
}

sealed trait AuthenticateResult
case class Success(user: User) extends AuthenticateResult
case class InvalidPassword(attemptsAllowed: Int) extends AuthenticateResult
object NonActivatedUserEmail extends AuthenticateResult
object UserNotFound extends AuthenticateResult
case class ToManyAuthenticateRequests(nextAllowedAttemptTime: Instant) extends AuthenticateResult
