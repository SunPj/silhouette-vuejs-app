package models

object UserRoles extends Enumeration {
  type UserRole = Value
  val User = Value(1)
  val Admin = Value(2)
}