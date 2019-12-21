package controllers

import javax.inject.Inject
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.Clock
import com.mohiva.play.silhouette.impl.providers._
import models.services.UserService
import play.api.Configuration
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
  * @param ex                     The execution context.
  */
class SocialAuthController @Inject()(components: ControllerComponents,
                                     silhouette: Silhouette[DefaultEnv],
                                     configuration: Configuration,
                                     clock: Clock,
                                     userService: UserService,
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
            user <- userService.save(profile)
            authInfo <- authInfoRepository.save(profile.loginInfo, authInfo)
            result <- authenticateUser(user, rememberMe = true)
          } yield result
        }
      case _ => Future.failed(new ProviderException(s"Cannot authenticate with unexpected social provider $provider"))
    }).recover {
      case e: ProviderException =>
        logger.error("Unexpected provider error", e)
        Redirect("/error?message=socialAuthFailed")
    }
  }
}