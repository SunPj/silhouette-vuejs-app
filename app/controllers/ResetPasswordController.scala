package controllers

import java.util.UUID

import javax.inject.Inject
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{PasswordHasherRegistry, PasswordInfo}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import forms.ResetPasswordForm
import models.services.{AuthTokenService, UserService}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}
import utils.auth.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}

/**
  * The `Reset Password` controller.
  *
  * @param components             The Play controller components.
  * @param silhouette             The Silhouette stack.
  * @param userService            The user service implementation.
  * @param authInfoRepository     The auth info repository.
  * @param passwordHasherRegistry The password hasher registry.
  * @param authTokenService       The auth token service implementation.
  * @param ex                     The execution context.
  */
class ResetPasswordController @Inject()(components: ControllerComponents,
                                        silhouette: Silhouette[DefaultEnv],
                                        userService: UserService,
                                        authInfoRepository: AuthInfoRepository,
                                        passwordHasherRegistry: PasswordHasherRegistry,
                                        authTokenService: AuthTokenService)
                                       (implicit ex: ExecutionContext) extends AbstractController(components) {

  /**
    * Resets the password.
    *
    * @param token The token to identify a user.
    * @return The result to display.
    */
  def submit(token: UUID) = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    authTokenService.validate(token).flatMap {
      case Some(authToken) =>
        ResetPasswordForm.form.bindFromRequest.fold(
          _ => Future.successful(BadRequest),
          password => userService.retrieve(authToken.userID).flatMap {
            case Some(user) if user.loginInfo.providerID == CredentialsProvider.ID =>
              val passwordInfo = passwordHasherRegistry.current.hash(password)
              authInfoRepository.update[PasswordInfo](user.loginInfo, passwordInfo).map { _ =>
                Ok
              }
            case _ => Future.successful(BadRequest(Json.obj("message" -> "Reset token is either invalid or has expired. Please reset your password again.")))
          }
        )
      case None => Future.successful(BadRequest(Json.obj("message" -> "Reset token is either invalid or has expired. Please reset your password again.")))
    }
  }
}