package utils.auth

import io.github.honeycombcheesecake.play.silhouette.api.Authorization
import io.github.honeycombcheesecake.play.silhouette.impl.authenticators.JWTAuthenticator
import models.User
import models.UserRoles.UserRole
import play.api.mvc.Request

import scala.concurrent.Future

case class HasRole(role: UserRole) extends Authorization[User, JWTAuthenticator] {
  override def isAuthorized[B](identity: User, authenticator: JWTAuthenticator)(implicit request: Request[B]) =
    Future.successful(identity.role == role)
}
