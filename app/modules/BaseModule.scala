package modules

import com.google.inject.{AbstractModule, Provides}
import javax.inject.Named
import models.services.captcha.{CaptchaService, ReCaptchaConfig, ReCaptchaService}
import models.services.{AuthTokenService, AuthTokenServiceImpl}
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration

/**
  * The base Guice module.
  */
class BaseModule extends AbstractModule with ScalaModule {

  /**
    * Configures the module.
    */
  override def configure(): Unit = {
    bind[AuthTokenService].to[AuthTokenServiceImpl]
    bind[CaptchaService].to[ReCaptchaService]
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
}