package utils.auth

import io.github.honeycombcheesecake.play.silhouette.api.Env
import io.github.honeycombcheesecake.play.silhouette.impl.authenticators.JWTAuthenticator
import models.User

/**
 * The default env.
 */
trait DefaultEnv extends Env {
  type I = User
  type A = JWTAuthenticator
}