package controllers

import java.util.UUID

import javax.inject.Inject
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.impl.providers._
import forms.SignUpForm
import models.services.captcha.CaptchaService
import models.{User, UserRoles}
import models.services.{AuthTokenService, MailService, UserService}
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}
import utils.auth.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}

/**
  * The `Sign Up` controller.
  *
  * @param components             The Play controller components.
  * @param silhouette             The Silhouette stack.
  * @param userService            The user service implementation.
  * @param authInfoRepository     The auth info repository implementation.
  * @param authTokenService       The auth token service implementation.
  * @param avatarService          The avatar service implementation.
  * @param passwordHasherRegistry The password hasher registry.
  * @param mailService           The mailer service.
  * @param ex                     The execution context.
  */
class SignUpController @Inject()(components: ControllerComponents,
                                 silhouette: Silhouette[DefaultEnv],
                                 userService: UserService,
                                 authInfoRepository: AuthInfoRepository,
                                 authTokenService: AuthTokenService,
                                 avatarService: AvatarService,
                                 captchaService: CaptchaService,
                                 passwordHasherRegistry: PasswordHasherRegistry,
                                 mailService: MailService)(implicit ex: ExecutionContext) extends AbstractController(components) with I18nSupport {

  /**
    * Handles the submitted form.
    *
    * @return The result to display.
    */
  def submit = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    SignUpForm.form.bindFromRequest.fold(
      _ => Future.successful(BadRequest),
      data => {
        captchaService.validate(data.captchaResponse, request.remoteAddress) flatMap { valid =>
          if (valid) {
            val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
            userService.retrieve(loginInfo).flatMap {
              case Some(user) =>
                Future.successful(Conflict)
              case None =>
                val authInfo = passwordHasherRegistry.current.hash(data.password)
                val user = User(
                  userID = UUID.randomUUID(),
                  loginInfo = loginInfo,
                  firstName = Some(data.firstName),
                  lastName = Some(data.lastName),
                  email = Some(data.email),
                  avatarURL = None,
                  activated = false,
                  role = UserRoles.User
                )
                for {
                  avatar <- avatarService.retrieveURL(data.email)
                  user <- userService.save(user.copy(avatarURL = avatar))
                  authInfo <- authInfoRepository.add(loginInfo, authInfo)
                  authToken <- authTokenService.create(user.userID)
                } yield {
                  val route = routes.ActivateAccountController.activate(authToken.id)
                  mailService.sendActivateAccountEmail(data.email, route.absoluteURL())
                  silhouette.env.eventBus.publish(SignUpEvent(user, request))
                  Ok
                }
            }
          } else {
            Future.successful(BadRequest("Captcha code is not correct"))
          }
        }
      }
    )
  }
}