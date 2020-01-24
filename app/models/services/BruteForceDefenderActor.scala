package models.services

import java.time.Instant

import akka.actor.{Actor, Cancellable}
import javax.inject.Inject

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration
import scala.language.postfixOps

class BruteForceDefenderActor @Inject()(conf: BruteForceDefenderConf)(implicit ex: ExecutionContext) extends Actor {
  import BruteForceDefenderActor._

  private var signInAttempts = Map.empty[String, List[Instant]]

  private var scheduler: Cancellable = _

  private case object Reset

  override def preStart(): Unit = {
    import scala.concurrent.duration._
    scheduler = context.system.scheduler.schedule(
      initialDelay = 1 second,
      interval = conf.period,
      receiver = self,
      message = Reset
    )
  }

  override def postStop(): Unit = {
    scheduler.cancel()
  }

  override def receive: Receive = {
    case IsSignInAllowed(email) =>
      val signInAttemptsForEmail = signInAttempts.getOrElse(email, List.empty)
      val attemptsAllowed = conf.attempts - signInAttemptsForEmail.size
      if (attemptsAllowed > 0) {
        sender() ! SignInAllowed(attemptsAllowed)
      } else {
        sender() ! SignInForbidden(signInAttemptsForEmail.last.plusSeconds(conf.period.toSeconds))
      }

    case RegisterWrongPasswordSignIn(email) =>
      signInAttempts += email -> (Instant.now :: signInAttempts.getOrElse(email, List.empty))

    case Reset =>
      val expiredBefore = Instant.now.minusSeconds(conf.period.toSeconds)
      signInAttempts = signInAttempts.map {
        case (email, attempts) => email -> attempts.filter(_.isAfter(expiredBefore))
      }
  }
}

/**
  * @param attempts SignIn attempts during the period
  * @param period period of time when we reset failed sign in attempts count, so user can continue trying
  */
case class BruteForceDefenderConf(attempts: Int, period: FiniteDuration)

object BruteForceDefenderActor {
  case class RegisterWrongPasswordSignIn(email: String)

  case class IsSignInAllowed(email: String)
  case class SignInAllowed(attemptsAllowed: Int)
  case class SignInForbidden(nextSignInAllowedAt: Instant)
}

