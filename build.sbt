import play.sbt.routes.RoutesKeys
RoutesKeys.routesImport := Seq.empty

name := "play-silhouette-vuejs"

version := "4.0.0"

scalaVersion := "2.13.8"

lazy val playSlickVersion = "5.1.0"
lazy val playSilhouetteVersion = "8.0.2"

resolvers += Resolver.jcenterRepo

libraryDependencies ++= Seq(
  "io.github.honeycomb-cheesecake" %% "play-silhouette" % playSilhouetteVersion,
  "io.github.honeycomb-cheesecake" %% "play-silhouette-password-bcrypt" % playSilhouetteVersion,
  "io.github.honeycomb-cheesecake" %% "play-silhouette-persistence" % playSilhouetteVersion,
  "io.github.honeycomb-cheesecake" %% "play-silhouette-crypto-jca" % playSilhouetteVersion,
  "io.github.honeycomb-cheesecake" %% "play-silhouette-totp" % playSilhouetteVersion,
  "net.codingwell" %% "scala-guice" % "5.1.0",
  "org.postgresql" % "postgresql" % "9.4.1211",
  "com.github.tminglei" %% "slick-pg" % "0.21.1",
  "com.github.tminglei" %% "slick-pg_play-json" % "0.21.1",
  "com.iheart" %% "ficus" % "1.5.2",
  "com.typesafe.play" %% "play-slick"               % playSlickVersion,
  "com.typesafe.play" %% "play-slick-evolutions"    % playSlickVersion,
  caffeine,
  guice,
  filters,
  "com.sendgrid" % "sendgrid-java" % "4.4.1"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

routesGenerator := InjectedRoutesGenerator

scalacOptions ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  "-Xlint", // Enable recommended additional warnings.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-numeric-widen" // Warn when numerics are widened.
)
