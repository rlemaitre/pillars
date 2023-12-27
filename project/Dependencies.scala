import sbt.*
object Dependencies {
  private val cats: Seq[ModuleID] = Seq(
    "org.typelevel" %% "cats-core"   % "2.10.0",
    "org.typelevel" %% "cats-effect" % "3.5.2"
  )

  private val fs2: Seq[ModuleID] = Seq(
    "co.fs2" %% "fs2-core" % "3.7.0"
  )

  private val json: Seq[ModuleID] = Seq(
    "io.circe" %% "circe-core"    % "0.14.5",
    "io.circe" %% "circe-generic" % "0.14.5",
    "io.circe" %% "circe-parser"  % "0.14.5"
  )

  private val http4s: Seq[ModuleID] = Seq(
    "org.http4s" %% "http4s-core"         % "0.23.24",
    "org.http4s" %% "http4s-dsl"          % "0.23.24",
    "org.http4s" %% "http4s-ember-server" % "0.23.24",
    "org.http4s" %% "http4s-ember-client" % "0.23.24",
    "org.http4s" %% "http4s-circe"        % "0.23.24"
  )

  private val iron: Seq[ModuleID] = Seq(
    "io.github.iltotore" %% "iron"         % "2.3.0",
    "io.github.iltotore" %% "iron-cats"    % "2.3.0",
    "io.github.iltotore" %% "iron-circe"   % "2.3.0",
    "io.github.iltotore" %% "iron-ciris"   % "2.3.0",
//    "io.github.iltotore" %% "iron-decline" % "2.3.0"
  )

  private val decline: Seq[ModuleID] = Seq(
    "com.monovore" %% "decline" % "2.4.1"
  )

  private val ciris: Seq[ModuleID] = Seq(
    "is.cir" %% "ciris" % "3.5.0"
  )

  private val scribe: Seq[ModuleID] = Seq(
    "com.outr" %% "scribe" % "3.13.0"
  )

  private val doobie: Seq[ModuleID] = Seq(
    "org.tpolecat" %% "doobie-core"     % "1.0.0-RC5",
    "org.tpolecat" %% "doobie-postgres" % "1.0.0-RC5"
  )

  private val fly4s: Seq[ModuleID] = Seq(
    "com.github.geirolz" %% "fly4s-core" % "0.0.19"
  )

  private val postgresql: Seq[ModuleID] = Seq(
    "org.postgresql" % "postgresql" % "42.5.4"
  )

  private val tests: Seq[ModuleID] = Seq(
    "org.scalameta"      %% "munit"               % "0.7.29" % Test,
    "org.typelevel"      %% "munit-cats-effect-3" % "1.0.7"  % Test,
    "org.scalameta"      %% "munit-scalacheck"    % "0.7.29" % Test,
    "io.github.iltotore" %% "iron-scalacheck"     % "2.3.0"  % Test
  )

  val all: Seq[ModuleID] =
    cats ++ fs2 ++ json ++ http4s ++ iron ++ decline ++ ciris ++ scribe ++ doobie ++ fly4s ++ postgresql ++ tests

}
