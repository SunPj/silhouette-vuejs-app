package models.daos

import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.impl.providers.oauth1.TwitterProvider
import com.mohiva.play.silhouette.impl.providers.oauth2.{FacebookProvider, GoogleProvider}
import javax.inject.Inject
import models.UserManagementModel
import play.api.db.slick.DatabaseConfigProvider
import utils.{GridRequest, GridResponse, SlickGridQuerySupport}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.matching.Regex

class UserManagementDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends DAOSlick with SlickGridQuerySupport {
  /**
    * @param gridRequest grid request params
    * @return grid result for user management grid
    */
  def fetchUsersGridData(gridRequest: GridRequest): Future[GridResponse[UserManagementModel]] = {
    import MyPostgresProfile.api._
    import utils.DynamicGridQuerySupport.GridQuerySupportImplicits

    val baseQuery = slickUsers.filterOpt(gridRequest.filter)((user, key) => (user.firstName like s"%$key%") ||
      (user.lastName like s"%$key%") ||
      (user.email like s"%$key%")
    )
      .join(slickUserLoginInfos).on(_.id === _.userID)
      .joinLeft(slickLoginInfos).on((q, li) => q._2.loginInfoId === li.id)
      .groupBy {
        case ((u, _), _) => u
      }
      .map {
        case (u, group) =>
          (u, group.map(_._2.map(_.providerID)).arrayAgg[String])
      }

    val gridQuery = baseQuery.toGridQuery.withSortableColumns {
      case "email" => {
        case (u, _) => u.email
      }
      case "lastName" => {
        case (u, _) => u.lastName
      }
      case "firstName" => {
        case (u, _) => u.firstName
      }
      case "role" => {
        case (u, _) => u.roleId
      }
      case "signedUpAt" => {
        case (u, _) => u.signedUpAt
      }
    }

    runGridQuery(gridQuery, gridRequest).map { gridResponse =>

      val userManagementModelData = gridResponse.data.map {
        case (u, providerIds) =>
          UserManagementModel(
            u.userID,
            u.firstName,
            u.lastName,
            u.email.map(maskEmail),
            u.roleId,
            u.activated,
            u.signedUpAt,
            providerIds.contains(CredentialsProvider.ID),
            providerIds.contains(GoogleProvider.ID),
            providerIds.contains(FacebookProvider.ID),
            providerIds.contains(TwitterProvider.ID))
      }

      gridResponse.copy(data = userManagementModelData)
    }
  }

  private val emailMaskingPattern: Regex = "^(.{3}).*@.*(.{5})$".r

  private def maskEmail(email: String) = {
    email match {
      case "admin@sunsongs.ru" => email
      case emailMaskingPattern(prefix, postfix) => s"$prefix...@...$postfix"
      case _ => email
    }
  }
}
