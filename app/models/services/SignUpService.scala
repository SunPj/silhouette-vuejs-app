package models.services

import java.util.UUID

import io.github.honeycombcheesecake.play.silhouette.api.services.AvatarService
import io.github.honeycombcheesecake.play.silhouette.api.util.PasswordHasherRegistry
import io.github.honeycombcheesecake.play.silhouette.api.LoginInfo
import io.github.honeycombcheesecake.play.silhouette.impl.providers.CredentialsProvider
import javax.inject.Inject
import models.services.captcha.CaptchaService
import models.User

import scala.concurrent.{ExecutionContext, Future}

/**
  *
  * @param captchaService         The captcha service
  * @param avatarService          The avatar service implementation.
  * @param passwordHasherRegistry The password hasher registry.
  * @param mailService            The mailer service.
  * @param authenticateService    The authenticate service
  * @param userService            The user service implementation.
  * @param authTokenService       The auth token service implementation.

  */
class SignUpService @Inject()(captchaService: CaptchaService,
                              avatarService: AvatarService,
                              authTokenService: AuthTokenService,
                              mailService: MailService,
                              authenticateService: AuthenticateService,
                              passwordHasherRegistry: PasswordHasherRegistry,
                              userService: UserService)(implicit ex: ExecutionContext) {

  def signUpByCredentials(data: CredentialsSingUpData, userIdAddress: String, activationUrlProvider: UUID => String): Future[SignUpResult] = {
    captchaService.validate(data.captchaResponse, userIdAddress) flatMap { valid =>
      if (valid) {
        val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
        userService.retrieve(loginInfo).flatMap {
          case Some(user) =>
            Future.successful(UserAlreadyExists)
          case None =>
            val authInfo = passwordHasherRegistry.current.hash(data.password)
            for {
              avatar <- avatarService.retrieveURL(data.email)
              user <- userService.createOrUpdate(loginInfo, data.email, Some(data.firstName), Some(data.lastName), avatar)
              _ <- authenticateService.addAuthenticateMethod(user.userID, loginInfo, authInfo)
              authToken <- authTokenService.create(user.userID)
            } yield {
              val activationUrl = activationUrlProvider(authToken.id)
              mailService.sendActivateAccountEmail(data.email, activationUrl)

              UserCreated(user)
            }
        }
      } else {
        Future.successful(InvalidRecaptchaCode)
      }
    }
  }
}

sealed trait SignUpResult
case object UserAlreadyExists extends SignUpResult
case class UserCreated(user: User) extends SignUpResult
case object InvalidRecaptchaCode extends SignUpResult

/**
  * CredentialsSingUpData
  *
  * @param firstName       The first name of a user.
  * @param lastName        The last name of a user.
  * @param email           The email of the user.
  * @param password        The password of the user.
  * @param captchaResponse captcha response
  */
case class CredentialsSingUpData(firstName: String,
                                 lastName: String,
                                 email: String,
                                 password: String,
                                 captchaResponse: String)
