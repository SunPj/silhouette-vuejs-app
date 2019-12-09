package forms

import play.api.data.Form
import play.api.data.Forms._

object UserRoleForm {
  val form = Form(
    single("roleId" -> number)
  )
}
