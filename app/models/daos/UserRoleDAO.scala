package models.daos

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.ExecutionContext

class UserRoleDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends DAOSlick {
  import profile.api._

  def getUserRole() = slickUserRoles.filter(_.name === "user").map(_.id).result.headOption.map{
    case Some(id) => id
    case None => throw new RuntimeException("User role 'user' is absent in db")
  }
}

