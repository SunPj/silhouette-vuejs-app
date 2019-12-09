package utils.auth

import com.mohiva.play.silhouette.api.Authorization
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import models.User
import models.UserRoles.UserRole
import play.api.mvc.Request

import scala.concurrent.Future

case class HasRole(role: UserRole) extends Authorization[User, JWTAuthenticator] {
  override def isAuthorized[B](identity: User, authenticator: JWTAuthenticator)(implicit request: Request[B]) =
    Future.successful(identity.role == role)
}
