import sbt.*
object Dependencies {
  private val effect: Seq[ModuleID] = Seq(
    "org.typelevel" %% "cats-core"             % "2.10.0",
    "org.typelevel" %% "cats-effect"           % "3.5.2",
    "co.fs2"        %% "fs2-core"              % "3.7.0",
    "org.typelevel" %% "cats-collections-core" % "0.9.8",
    "org.typelevel" %% "cats-time"             % "0.5.1",
    "org.typelevel" %% "mouse"                 % "1.2.2"
  )

  private val model: Seq[ModuleID] = Seq(
    "com.comcast"        %% "ip4s-core"    % "3.4.0",
    "io.github.iltotore" %% "iron"         % "2.4.0",
    "io.github.iltotore" %% "iron-cats"    % "2.4.0",
    "io.github.iltotore" %% "iron-circe"   % "2.4.0",
    "io.github.iltotore" %% "iron-ciris"   % "2.4.0",
    "io.github.iltotore" %% "iron-decline" % "2.4.0"
  )

  private val commandLine: Seq[ModuleID] = Seq(
    "com.monovore" %% "decline"        % "2.4.1",
    "com.monovore" %% "decline-effect" % "2.4.1"
  )

  private val json: Seq[ModuleID] = Seq(
    "io.circe" %% "circe-core"    % "0.14.6",
    "io.circe" %% "circe-generic" % "0.14.6",
    "io.circe" %% "circe-parser"  % "0.14.6",
    "io.circe" %% "circe-yaml"    % "0.15.1"
  )

  private val http4s: Seq[ModuleID] = Seq(
    "org.http4s" %% "http4s-core"         % "0.23.24",
    "org.http4s" %% "http4s-dsl"          % "0.23.24",
    "org.http4s" %% "http4s-ember-server" % "0.23.24",
    "org.http4s" %% "http4s-ember-client" % "0.23.24",
    "org.http4s" %% "http4s-circe"        % "0.23.24"
  )

  private val scodec: Seq[ModuleID] = Seq(
    "org.scodec" %% "scodec-bits" % "2.2.2",
    "org.scodec" %% "scodec-core" % "2.2.2"
  )

  private val tapir = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % "1.9.1",
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe"    % "1.9.1",
//    "com.softwaremill.sttp.tapir"   %% "tapir-swagger-ui-bundle" % "1.9.1",
    "com.softwaremill.sttp.tapir"   %% "tapir-http4s-client"    % "1.9.1" % Test,
    "com.softwaremill.sttp.tapir"   %% "tapir-sttp-stub-server" % "1.9.1" % Test,
    "com.softwaremill.sttp.client3" %% "core"                   % "3.9.1" % Test
  )

  private val logging: Seq[ModuleID] = Seq( //
    "com.outr" %% "scribe"            % "3.13.0",
    "com.outr" %% "scribe-cats"       % "3.13.0",
    "com.outr" %% "scribe-slf4j"      % "3.13.0",
    "com.outr" %% "scribe-json-circe" % "3.13.0",
    "com.outr" %% "scribe-file"       % "3.13.0"
  )

  private val database: Seq[ModuleID] = Seq(
    "org.tpolecat" %% "skunk-core"  % "1.1.0-M2",
    "org.tpolecat" %% "skunk-circe" % "1.1.0-M2"
//    "dev.rolang" %% "dumbo" % "0.0.6" //enable when dumbo uses skunk 1.1.0-M2
  )

  private val tests: Seq[ModuleID] = Seq(
    "org.scalameta"      %% "munit"               % "0.7.29" % Test,
    "org.typelevel"      %% "munit-cats-effect-3" % "1.0.7"  % Test,
    "org.scalameta"      %% "munit-scalacheck"    % "0.7.29" % Test,
    "io.github.iltotore" %% "iron-scalacheck"     % "2.3.0"  % Test
  )

  private val observability: Seq[ModuleID] = Seq(
    "org.typelevel"   %% "otel4s-java"                               % "0.3.0",
    "io.opentelemetry" % "opentelemetry-exporter-otlp"               % "1.33.0" % Runtime,
    "io.opentelemetry" % "opentelemetry-sdk-extension-autoconfigure" % "1.33.0" % Runtime
  )

  val all: Seq[ModuleID] =
    effect ++ json ++ tapir ++ http4s ++ model ++ commandLine ++ logging ++ database ++ observability ++ tests

}
