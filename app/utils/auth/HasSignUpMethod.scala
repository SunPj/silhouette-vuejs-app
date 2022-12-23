package utils.auth

import io.github.honeycombcheesecake.play.silhouette.api.{Authenticator, Authorization}
import javax.inject.Inject
import models.User
import models.services.AuthenticateService
import play.api.mvc.Request

import scala.concurrent.Future

class HasSignUpMethod @Inject()(authenticateService: AuthenticateService) {

  /**
    * Grants only access if a user has authentication method with the given provider
    *
    * @param provider The provider ID the user must have authentication method with.
    * @tparam A The type of the authenticator.
    */
  case class HasMethod[A <: Authenticator](provider: String) extends Authorization[User, A] {
    /**
      * Indicates if a user is authorized to access an action.
      *
      * @param user          The usr object.
      * @param authenticator The authenticator instance.
      * @param request       The current request.
      * @tparam B The type of the request body.
      * @return True if the user is authorized, false otherwise.
      */
    override def isAuthorized[B](user: User, authenticator: A)(implicit request: Request[B]): Future[Boolean] = {
      authenticateService.userHasAuthenticationMethod(user.userID, provider)
    }
  }

  def apply[A <: Authenticator](provider: String): Authorization[User, A] = HasMethod[A](provider)
}
