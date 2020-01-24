package modules

import com.google.inject.{AbstractModule, Provides}
import javax.inject.Named
import models.services.captcha.{CaptchaService, ReCaptchaConfig, ReCaptchaService}
import models.services.{AuthTokenService, AuthTokenServiceImpl, BruteForceDefenderActor, BruteForceDefenderConf}
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration
import play.api.libs.concurrent.AkkaGuiceSupport

import scala.concurrent.duration._
import scala.concurrent.duration.FiniteDuration

/**
  * The base Guice module.
  */
class BaseModule extends AbstractModule with ScalaModule with AkkaGuiceSupport {

  /**
    * Configures the module.
    */
  override def configure(): Unit = {
    bind[AuthTokenService].to[AuthTokenServiceImpl]
    bind[CaptchaService].to[ReCaptchaService]
    bindActor[BruteForceDefenderActor]("brute-force-defender")
  }

  @Named("SendGridApiKey")
  @Provides
  def providesSendGridApiKey(conf: Configuration): String = {
    conf.get[String]("sendgrid.api.key")
  }

  @Provides
  def providesReCaptchaConfig(conf: Configuration): ReCaptchaConfig = {
    ReCaptchaConfig(conf.get[String]("recaptcha.secretKey"))
  }

  @Provides
  def providesBruteForceDefenderConf(conf: Configuration): BruteForceDefenderConf = {
    val attempts = conf.getOptional[Int]("signin.limit.attempts").getOrElse(5)
    val period = conf.getOptional[FiniteDuration]("signin.limit.period").getOrElse(30.minutes)
    BruteForceDefenderConf(attempts, period)
  }
}