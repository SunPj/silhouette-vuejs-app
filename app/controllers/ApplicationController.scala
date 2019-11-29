package controllers

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.{LogoutEvent, Silhouette}
import javax.inject.Inject
import models.services.IndexRenderService
import play.api.{Environment, Mode}
import play.api.http.ContentTypes
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc._
import utils.auth.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}

/**
  * The basic application controller.
  *
  * @param components The Play controller components.
  * @param silhouette The Silhouette stack.
  */
class ApplicationController @Inject()(components: ControllerComponents,
                                      silhouette: Silhouette[DefaultEnv],
                                      environment: Environment,
                                      ws: WSClient,
                                      indexRenderService: IndexRenderService,
                                      authInfoRepository: AuthInfoRepository)(implicit ec: ExecutionContext)
  extends AbstractController(components) with I18nSupport {

  /**
    * @return vuejs index.html page with CSRF set
    */
  def vueapp(path: String) = silhouette.UserAwareAction.async { implicit req =>
    environment.mode match {
      case Mode.Dev =>
        ws.url(s"http://localhost:8080/$path").get().map{ r =>
          new Status(r.status)(r.bodyAsBytes.utf8String).as(r.contentType)
        }
      case _ =>
        Future.successful{
          val html = indexRenderService.render(Some("SafeLoan"))
          Ok(html).as(ContentTypes.HTML)
        }
    }

  }

  /**
    * Returns the user.
    *
    * @return The result to display.
    */
  def user = silhouette.SecuredAction.async { implicit request =>
    Future.successful(Ok(Json.toJson(request.identity)))
  }

  /**
    * Handles the Sign Out action.
    *
    * @return The result to display.
    */
  def signOut = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))
    silhouette.env.authenticatorService.discard(request.authenticator, Ok)
  }
}
