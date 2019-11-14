package controllers

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.{LogoutEvent, Silhouette}
import javax.inject.Inject
import models.services.IndexRenderService
import play.api.http.ContentTypes
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents}
import utils.auth.DefaultEnv

import scala.concurrent.Future

/**
  * The basic application controller.
  *
  * @param components The Play controller components.
  * @param silhouette The Silhouette stack.
  */
class ApplicationController @Inject()(components: ControllerComponents,
                                      silhouette: Silhouette[DefaultEnv],
                                      indexRenderService: IndexRenderService,
                                      authInfoRepository: AuthInfoRepository)
  extends AbstractController(components) with I18nSupport {

  /**
    * @return vuejs index.html page with CSRF set
    */
  def vueapp(path: String) = silhouette.UserAwareAction { implicit req =>
    val html = indexRenderService.render(Some("SafeLoan"))
    Ok(html).as(ContentTypes.HTML)
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
