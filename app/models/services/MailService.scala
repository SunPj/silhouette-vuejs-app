package models.services

import com.sendgrid.{Method, Request, SendGrid}
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.{Content, Email}
import javax.inject.{Inject, Named}
import play.api.Logging

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Failure

class MailService @Inject()(@Named("SendGridApiKey") apiKey: String)(implicit ec: ExecutionContext) extends Logging {
  private val sendgrid = new SendGrid(apiKey)
  private val from = "noreply@mysitenamehere.com"

  def sendResetPasswordEmail(email: String, url: String): Unit = {
    sendHtmlEmail(
      from,
      email,
      "Site password reset",
      "<html><body><p>Please <a href='" + url + "' rel='nofollow'>click here</a> to reset your password.</p><p>If you didn't request password reset, please ignore this mail.</p></body></html>",
      "Site password reset"
    )
  }

  def sendActivateAccountEmail(email: String, url: String): Unit = {
    sendHtmlEmail(
      from,
      email,
      "Account confirmation",
      "<html><body><p>Please <a href='" + url + "' rel='nofollow'>click here</a> to confirm your account.</p><p>If you didn't create an account using this e-mail address, please ignore this message.</p></body></html>",
      "Account confirmation"
    )
  }

  private def sendHtmlEmail(from: String, to: String, subject: String, htmlContent: String, loggerNote: String) = {
    val fromEmail = new Email(from)
    val toEmail = new Email(to)
    val content = new Content("text/html", htmlContent)
    val mail = new Mail(fromEmail, subject, toEmail, content)

    Future {
      val request = new Request()
      request.setMethod(Method.POST)
      request.setEndpoint("mail/send")
      request.setBody(mail.build)
      val response = sendgrid.api(request)

      logger.info(s"Sending $loggerNote email to $to. Status: ${response.getStatusCode}")
    }.onComplete {
      case Failure(e) =>
        logger.error(s"Error on sending $loggerNote email to $to", e)
      case _ =>
        logger.info(s"$loggerNote email has been sent to $to")
    }
  }
}
