package models.daos

import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.impl.providers.oauth1.TwitterProvider
import com.mohiva.play.silhouette.impl.providers.oauth2.{FacebookProvider, GoogleProvider}
import javax.inject.Inject
import models.UserManagementModel
import play.api.db.slick.DatabaseConfigProvider
import utils.{GridRequest, GridResponse, SlickGridQuerySupport}

import scala.concurrent.{ExecutionContext, Future}

class UserManagementDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends DAOSlick with SlickGridQuerySupport {
  import profile.api._

  /**
    * @param gridRequest grid request params
    * @return grid result for user management grid
    */
  def fetchUsersGridData(gridRequest: GridRequest): Future[GridResponse[UserManagementModel]] = {
    import utils.DynamicGridQuerySupport.GridQuerySupportImplicits

    val baseQuery = slickUsers.filterOpt(gridRequest.filter)((user, key) => (user.firstName like s"%$key%") ||
      (user.lastName like s"%$key%") ||
      (user.email like s"%$key%")
    )
      .join(slickUserLoginInfos).on(_.id === _.userID)
      .joinLeft(slickLoginInfos).on((q, li) => q._2.loginInfoId === li.id && li.providerID === CredentialsProvider.ID)
      .joinLeft(slickLoginInfos).on((q, li) => q._1._2.loginInfoId === li.id && li.providerID === GoogleProvider.ID)
      .joinLeft(slickLoginInfos).on((q, li) => q._1._1._2.loginInfoId === li.id && li.providerID === FacebookProvider.ID)
      .joinLeft(slickLoginInfos).on((q, li) => q._1._1._1._2.loginInfoId === li.id && li.providerID === TwitterProvider.ID)

    val gridQuery = baseQuery.toGridQuery.withSortableColumns {
      case "email" => {
        case (((((u, _), _), _), _), _) => u.email
      }
      case "lastName" => {
        case (((((u, _), _), _), _), _) => u.lastName
      }
      case "firstName" => {
        case (((((u, _), _), _), _), _) => u.firstName
      }
      case "role" => {
        case (((((u, _), _), _), _), _) => u.roleId
      }
      case "signedUpAt" => {
        case (((((u, _), _), _), _), _) => u.signedUpAt
      }
    }

    runGridQuery(gridQuery, gridRequest).map { gridResponse =>

      val userManagementModelData = gridResponse.data.map {
        case (((((u, _), cred), google), fb), twitter) =>
          UserManagementModel(u.userID, u.firstName, u.lastName, u.email, u.roleId, u.activated, u.signedUpAt,
            cred.isDefined, google.isDefined, fb.isDefined, twitter.isDefined)
      }

      gridResponse.copy(data = userManagementModelData)
    }
  }
}
