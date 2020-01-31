package controllers

import javax.inject.Inject
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.Clock
import com.mohiva.play.silhouette.impl.providers._
import models.services._
import play.api.Configuration
import play.api.libs.json.Json
import play.api.mvc.{AnyContent, ControllerComponents, Request}
import utils.auth.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}

/**
  * The social auth controller.
  *
  * @param components             The Play controller components.
  * @param silhouette             The Silhouette stack.
  * @param userService            The user service implementation.
  * @param authInfoRepository     The auth info service implementation.
  * @param socialProviderRegistry The social provider registry.
  * @param authenticateService    authenticate service
  * @param ex                     The execution context.
  */
class SocialAuthController @Inject()(components: ControllerComponents,
                                     silhouette: Silhouette[DefaultEnv],
                                     configuration: Configuration,
                                     clock: Clock,
                                     userService: UserService,
                                     authenticateService: AuthenticateService,
                                     authInfoRepository: AuthInfoRepository,
                                     socialProviderRegistry: SocialProviderRegistry)
                                    (implicit ex: ExecutionContext) extends AbstractAuthController(silhouette, configuration, clock) with Logger {

  /**
    * Authenticates a user against a social provider.
    *
    * @param provider The ID of the provider to authenticate against.
    * @return The result to display.
    */
  def authenticate(provider: String) = Action.async { implicit request: Request[AnyContent] =>
    (socialProviderRegistry.get[SocialProvider](provider) match {
      case Some(p: SocialProvider with CommonSocialProfileBuilder) =>
        p.authenticate().flatMap {
          case Left(result) => Future.successful(result)
          case Right(authInfo) => for {
            profile <- p.retrieveProfile(authInfo)
            userBindResult <- authenticateService.provideUserForSocialAccount(provider, profile, authInfo)
            result <- userBindResult match {
              case AccountBound(u) =>
                authenticateUser(u, profile.loginInfo, rememberMe = true)
              case EmailIsBeingUsed(providers) =>
                Future.successful(Conflict(Json.obj("error" -> "EmailIsBeingUsed", "providers" -> providers)))
              case NoEmailProvided =>
                Future.successful(BadRequest(Json.obj("error" -> "NoEmailProvided")))
            }
          } yield result
        }
      case _ => Future.failed(new ProviderException(s"Cannot authenticate with unexpected social provider $provider"))
    }).recover {
      case e: ProviderException =>
        logger.error("Unexpected provider error", e)
        Redirect("/error?message=socialAuthFailed")
    }
  }

  def linkAccount(provider: String) = silhouette.SecuredAction.async { implicit req =>
    (socialProviderRegistry.get[SocialProvider](provider) match {
      case Some(p: SocialProvider with CommonSocialProfileBuilder) =>
        p.authenticate().flatMap {
          case Left(result) =>
            Future.successful(result)
          case Right(authInfo) =>  for {
            profile <- p.retrieveProfile(authInfo)
            _ <- authenticateService.addAuthenticateMethod(req.identity.userID, profile.loginInfo, authInfo)
          } yield Ok
        }
      case _ => Future.failed(new ProviderException(s"Cannot authenticate with unexpected social provider $provider"))
    }).recover {
      case e: ProviderException =>
        logger.error("Unexpected provider error", e)
        Redirect("/error?message=socialAuthFailed")
    }
  }
}
