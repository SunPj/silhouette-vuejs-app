package modules

import com.google.inject.{AbstractModule, Provides}
import javax.inject.Named
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
  }

  @Named("SendGridApiKey")
  @Provides
  def providesSendGridApiKey(conf: Configuration): String = {
    conf.get[String]("sendgrid.api.key")
  }
}