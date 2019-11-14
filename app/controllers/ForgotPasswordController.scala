package controllers

import javax.inject.Inject
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import forms.ForgotPasswordForm
import models.services.{AuthTokenService, MailService, UserService}
import play.api.mvc._
import utils.auth.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}

/**
  * The `Forgot Password` controller.
  *
  * @param components       The Play controller components.
  * @param silhouette       The Silhouette stack.
  * @param userService      The user service implementation.
  * @param authTokenService The auth token service implementation.
  * @param mailService      The mail service.
  * @param ex               The execution context.
  */
class ForgotPasswordController @Inject()(components: ControllerComponents,
                                         silhouette: Silhouette[DefaultEnv],
                                         userService: UserService,
                                         authTokenService: AuthTokenService,
                                         mailService: MailService)(implicit ex: ExecutionContext) extends AbstractController(components) {

  /**
    * Sends an email with password reset instructions.
    *
    * It sends an email to the given address if it exists in the database. Otherwise we do not show the user
    * a notice for not existing email addresses to prevent the leak of existing email addresses.
    *
    * @return The result to display.
    */
  def submit = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    ForgotPasswordForm.form.bindFromRequest.fold(
      _ => Future.successful(BadRequest),
      email => {
        val loginInfo = LoginInfo(CredentialsProvider.ID, email)

        userService.retrieve(loginInfo).flatMap {
          case Some(user) if user.email.isDefined =>
            authTokenService.create(user.userID).map { authToken =>
              val route = Call("GET", s"/reset-password?token=${authToken.id.toString}")
              mailService.sendResetPasswordEmail(user.email.get, route.absoluteURL())

              Ok
            }
          case None => Future.successful(Ok)
        }
      }
    )
  }
}