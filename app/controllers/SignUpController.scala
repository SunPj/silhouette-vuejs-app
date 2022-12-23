package controllers

import java.util.UUID

import javax.inject.Inject
import io.github.honeycombcheesecake.play.silhouette.api._
import forms.SignUpForm
import models.services._
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}
import utils.auth.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}

/**
  * The `Sign Up` controller.
  *
  * @param components             The Play controller components.
  * @param silhouette             The Silhouette stack.
  * @param ex                     The execution context.
  */
class SignUpController @Inject()(components: ControllerComponents,
                                 silhouette: Silhouette[DefaultEnv],
                                 signUpService: SignUpService,
                                 )(implicit ex: ExecutionContext) extends AbstractController(components) with I18nSupport {

  /**
    * Handles the submitted form.
    *
    * @return The result to display.
    */
  def submit = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    SignUpForm.form.bindFromRequest().fold(
      _ => Future.successful(BadRequest),
      data => {
        val activationUrlProvider: UUID => String = authTokenId => routes.ActivateAccountController.activate(authTokenId).absoluteURL()

        signUpService.signUpByCredentials(data, request.remoteAddress, activationUrlProvider).map {
          case UserCreated(user) =>
            silhouette.env.eventBus.publish(SignUpEvent(user, request))
            Ok
          case UserAlreadyExists =>
            Conflict
          case InvalidRecaptchaCode =>
            BadRequest("Captcha code is not correct")
        }
      }
    )
  }
}
