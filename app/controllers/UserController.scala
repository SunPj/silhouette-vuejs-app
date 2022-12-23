package controllers

import java.util.UUID

import io.github.honeycombcheesecake.play.silhouette.api.Silhouette
import javax.inject.Inject
import models.services.AuthenticateService
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import utils.auth.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}

class UserController @Inject()(silhouette: Silhouette[DefaultEnv],
                               authenticateService: AuthenticateService,
                               components: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(components) {

  def fetchLinkedAccounts(userId: UUID) = silhouette.SecuredAction.async { implicit req =>
    if (userId == req.identity.userID) {
      authenticateService.getAuthenticationProviders(req.identity.email.get).map(providers => Ok(Json.toJson(providers)))
    } else {
      Future.successful(Forbidden)
    }
  }
}
