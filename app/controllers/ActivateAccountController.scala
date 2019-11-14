package controllers

import java.net.URLDecoder
import java.util.UUID

import javax.inject.Inject
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.services.{AuthTokenService, MailService, UserService}
import play.api.mvc.{AbstractController, ControllerComponents}
import utils.auth.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}

/**
  * The `Activate Account` controller.
  *
  * @param components       The Play controller components.
  * @param silhouette       The Silhouette stack.
  * @param userService      The user service implementation.
  * @param authTokenService The auth token service implementation.
  * @param ex               The execution context.
  */
class ActivateAccountController @Inject()(components: ControllerComponents,
                                          silhouette: Silhouette[DefaultEnv],
                                          userService: UserService,
                                          authTokenService: AuthTokenService,
                                          mailService: MailService)(implicit ex: ExecutionContext) extends AbstractController(components) {

  /**
    * Sends an account activation email to the user with the given email.
    *
    * @param email The email address of the user to send the activation mail to.
    * @return The result to display.
    */
  def send(email: String) = silhouette.UnsecuredAction.async { implicit r =>
    val decodedEmail = URLDecoder.decode(email, "UTF-8")
    val loginInfo = LoginInfo(CredentialsProvider.ID, decodedEmail)

    userService.retrieve(loginInfo).flatMap {
      case Some(user) if !user.activated =>
        authTokenService.create(user.userID).map { authToken =>
          val route = routes.ActivateAccountController.activate(authToken.id)
          mailService.sendActivateAccountEmail(decodedEmail, route.absoluteURL())
          Ok
        }
      case None => Future.successful(Ok)
    }
  }

  /**
    * Activates an account.
    *
    * @param token The token to identify a user.
    * @return The result to display.
    */
  def activate(token: UUID) = silhouette.UnsecuredAction.async {
    authTokenService.validate(token).flatMap {
      case Some(authToken) => userService.retrieve(authToken.userID).flatMap {
        case Some(user) if user.loginInfo.providerID == CredentialsProvider.ID =>
          userService.save(user.copy(activated = true)).map { _ =>
            Redirect("/signin?message=emailVerified")
          }
        case _ => Future.successful(Redirect("/error?message=activationTokenInvalid"))
      }
      case None => Future.successful(Redirect("/error?message=activationTokenInvalid"))
    }
  }
}
