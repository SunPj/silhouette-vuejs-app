package controllers

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.util.Clock
import forms.SignInForm
import javax.inject.Inject
import models.services._
import play.api.Configuration
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.{AnyContent, Request}
import utils.auth.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}

/**
  * The `Sign In` controller.
  *
  * @param silhouette             The Silhouette stack.
  * @param configuration          The Play configuration.
  * @param clock                  The clock instance.
  */
class SignInController @Inject()(authenticateService: AuthenticateService,
                                 silhouette: Silhouette[DefaultEnv],
                                 configuration: Configuration,
                                 clock: Clock)(implicit ex: ExecutionContext)
  extends AbstractAuthController(silhouette, configuration, clock) with I18nSupport with Logger {

  /**
    * Handles the submitted form.
    *
    * @return The result to display.
    */
  def submit = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    SignInForm.form.bindFromRequest.fold(
      _ => Future.successful(BadRequest),
      data => {
        authenticateService.credentials(data.email, data.password).flatMap {
          case Success(user) =>
            authenticateUser(user, data.rememberMe)
          case InvalidPassword(attemptsAllowed) =>
            Future.successful(Forbidden(Json.obj("errorCode" -> "InvalidPassword", "attemptsAllowed" -> attemptsAllowed)))
          case NonActivatedUserEmail =>
            Future.successful(Forbidden(Json.obj("errorCode" -> "NonActivatedUserEmail")))
          case UserNotFound =>
            Future.successful(Forbidden(Json.obj("errorCode" -> "UserNotFound")))
          case ToManyAuthenticateRequests(nextAllowedAttemptTime) =>
            Future.successful(TooManyRequests(Json.obj("errorCode" -> "TooManyRequests", "nextAllowedAttemptTime" -> nextAllowedAttemptTime)))
        }
        .recover {
          case e =>
            logger.error(s"Sign in error email = ${data.email}", e)
            InternalServerError(Json.obj("errorCode" -> "SystemError"))
        }
      }
    )
  }
}
