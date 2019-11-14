package controllers

import javax.inject.Inject
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{Credentials, PasswordHasherRegistry, PasswordInfo}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import forms.ChangePasswordForm
import play.api.i18n.{I18nSupport, Messages}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents}
import utils.auth.{DefaultEnv, WithProvider}

import scala.concurrent.{ExecutionContext, Future}

/**
  * The `Change Password` controller.
  *
  * @param components             The Play controller components.
  * @param silhouette             The Silhouette stack.
  * @param credentialsProvider    The credentials provider.
  * @param authInfoRepository     The auth info repository.
  * @param passwordHasherRegistry The password hasher registry.
  * @param ex                     The execution context.
  */
class ChangePasswordController @Inject()(components: ControllerComponents,
                                         silhouette: Silhouette[DefaultEnv],
                                         credentialsProvider: CredentialsProvider,
                                         authInfoRepository: AuthInfoRepository,
                                         passwordHasherRegistry: PasswordHasherRegistry)(implicit ex: ExecutionContext) extends AbstractController(components) with I18nSupport {
  /**
    * Changes the password.
    *
    * @return The result to display.
    */
  def submit = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID)).async {
    implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
      ChangePasswordForm.form.bindFromRequest.fold(
        form => Future.successful(BadRequest),
        password => {
          val (currentPassword, newPassword) = password
          val credentials = Credentials(request.identity.email.getOrElse(""), currentPassword)
          credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
            val passwordInfo = passwordHasherRegistry.current.hash(newPassword)
            authInfoRepository.update[PasswordInfo](loginInfo, passwordInfo).map { _ =>
              Ok(Json.obj("success" -> Messages("password.changed")))
            }
          }.recover {
            case _: ProviderException =>
              BadRequest(Json.obj("message" -> Messages("current.password.invalid")))
          }
        }
      )
  }
}
