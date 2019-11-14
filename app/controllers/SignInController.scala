package controllers

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{Clock, Credentials}
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers._
import forms.SignInForm
import javax.inject.Inject
import models.services.UserService
import play.api.Configuration
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.{AnyContent, ControllerComponents, Request}
import utils.auth.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}

/**
  * The `Sign In` controller.
  *
  * @param components             The Play controller components.
  * @param silhouette             The Silhouette stack.
  * @param userService            The user service implementation.
  * @param credentialsProvider    The credentials provider.
  * @param socialProviderRegistry The social provider registry.
  * @param configuration          The Play configuration.
  * @param clock                  The clock instance.
  */
class SignInController @Inject()(components: ControllerComponents,
                                 silhouette: Silhouette[DefaultEnv],
                                 userService: UserService,
                                 authInfoRepository: AuthInfoRepository,
                                 credentialsProvider: CredentialsProvider,
                                 socialProviderRegistry: SocialProviderRegistry,
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
      form => Future.successful(BadRequest),
      data => {
        val credentials = Credentials(data.email, data.password)
        credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
          userService.retrieve(loginInfo).flatMap {
            case Some(user) if !user.activated =>
              Future.successful(Forbidden(Json.obj("message" -> "Email has not been confirmed yet")))
            case Some(user) =>
              authenticateUser(user, data.rememberMe)
            case None =>
              Future.failed(new IdentityNotFoundException("Couldn't find user"))
          }
        }.recover {
          case e: ProviderException =>
            logger.error("Sigin error", e)
            Forbidden(Json.obj("message" -> "Wrong email or password"))
        }
      }
    )
  }
}
