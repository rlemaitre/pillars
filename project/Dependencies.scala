import sbt.*
object Dependencies {
    val effect: Seq[ModuleID] = Seq(
      "org.typelevel" %% "cats-core"             % "2.10.0",
      "org.typelevel" %% "cats-effect"           % "3.5.4",
      "co.fs2"        %% "fs2-core"              % "3.9.4",
      "org.typelevel" %% "cats-collections-core" % "0.9.8",
      "org.typelevel" %% "cats-time"             % "0.5.1",
      "org.typelevel" %% "mouse"                 % "1.2.3"
    )

    val model: Seq[ModuleID] = Seq(
      "com.comcast"        %% "ip4s-core"    % "3.5.0",
      "io.github.iltotore" %% "iron"         % "2.5.0",
      "io.github.iltotore" %% "iron-cats"    % "2.5.0",
      "io.github.iltotore" %% "iron-circe"   % "2.5.0",
      "io.github.iltotore" %% "iron-decline" % "2.5.0"
    )

    val commandLine: Seq[ModuleID] = Seq(
      "com.monovore" %% "decline"        % "2.4.1",
      "com.monovore" %% "decline-effect" % "2.4.1"
    )

    val json: Seq[ModuleID] = Seq(
      "io.circe" %% "circe-core"    % "0.14.6",
      "io.circe" %% "circe-generic" % "0.14.6",
      "io.circe" %% "circe-parser"  % "0.14.6",
      "io.circe" %% "circe-yaml"    % "0.15.1"
    )

    val http4s: Seq[ModuleID] = Seq(
      "org.http4s" %% "http4s-core"  % "0.23.26",
      "org.http4s" %% "http4s-dsl"   % "0.23.26",
      "org.http4s" %% "http4s-circe" % "0.23.26"
    )

    val http4sClient: Seq[ModuleID] = Seq(
      "org.http4s" %% "http4s-netty-client" % "0.5.15"
    )
    val http4sServer: Seq[ModuleID] = Seq(
      "org.http4s" %% "http4s-netty-server" % "0.5.15"
    )
    val scodec: Seq[ModuleID]       = Seq(
      "org.scodec" %% "scodec-bits" % "2.2.2",
      "org.scodec" %% "scodec-core" % "2.2.2"
    )

    private val tapir = Seq(
      "com.softwaremill.sttp.tapir"   %% "tapir-http4s-server"         % "1.9.11",
      "com.softwaremill.sttp.tapir"   %% "tapir-json-circe"            % "1.9.11",
      "com.softwaremill.sttp.tapir"   %% "tapir-opentelemetry-metrics" % "1.9.11",
      "com.softwaremill.sttp.tapir"   %% "tapir-iron"                  % "1.9.11",
      "com.softwaremill.sttp.tapir"   %% "tapir-openapi-docs"          % "1.9.11",
      "com.softwaremill.sttp.apispec" %% "openapi-circe-yaml"          % "0.7.4",
      "com.softwaremill.sttp.tapir"   %% "tapir-http4s-client"         % "1.9.11" % Test,
      "com.softwaremill.sttp.tapir"   %% "tapir-sttp-stub-server"      % "1.9.11" % Test,
      "com.softwaremill.sttp.client3" %% "core"                        % "3.9.4"  % Test
      //    "com.softwaremill.sttp.tapir"   %% "tapir-swagger-ui-bundle" % "1.9.1",
    )

    val logging: Seq[ModuleID] = Seq( //
      "com.outr" %% "scribe"            % "3.13.0",
      "com.outr" %% "scribe-cats"       % "3.13.0",
      "com.outr" %% "scribe-slf4j"      % "3.13.0",
      "com.outr" %% "scribe-json-circe" % "3.13.0",
      "com.outr" %% "scribe-file"       % "3.13.0"
    )

    val tests: Seq[ModuleID] = Seq(
      "org.scalameta"      %% "munit"             % "1.0.0-M11" % Test,
      "org.typelevel"      %% "munit-cats-effect" % "2.0.0-M4"  % Test,
      "org.scalameta"      %% "munit-scalacheck"  % "1.0.0-M11" % Test,
      "io.github.iltotore" %% "iron-scalacheck"   % "2.5.0"     % Test
    )

    val testContainers: Seq[ModuleID] = Seq(
      "com.dimafeng" %% "testcontainers-scala-munit"      % "0.41.3" % Test,
      "com.dimafeng" %% "testcontainers-scala-postgresql" % "0.41.3" % Test,
      "com.dimafeng" %% "testcontainers-scala-rabbitmq"   % "0.41.3" % Test
    )

    val observability: Seq[ModuleID] = Seq(
      "org.typelevel"   %% "otel4s-java"                               % "0.4.0",
      "io.opentelemetry" % "opentelemetry-exporter-otlp"               % "1.36.0" % Runtime,
      "io.opentelemetry" % "opentelemetry-sdk-extension-autoconfigure" % "1.36.0" % Runtime
    )

    val database: Seq[ModuleID] = Seq(
      "org.tpolecat" %% "skunk-core"  % "1.0.0-M4",
      "org.tpolecat" %% "skunk-circe" % "1.0.0-M4"
    ) ++ tests

    val doobie: Seq[ModuleID] = Seq(
      "org.tpolecat" %% "doobie-core"   % "1.0.0-RC4",
      "org.tpolecat" %% "doobie-hikari" % "1.0.0-RC4" // HikariCP transactor.
    ) ++ tests

    val fs2Rabbit: Seq[ModuleID] = Seq(
      "dev.profunktor" %% "fs2-rabbit" % "5.1.0"
    ) ++ tests ++ testContainers

    val migrations: Seq[ModuleID] = Seq(
      "dev.rolang" %% "dumbo" % "0.1.0"
    ) ++ tests ++ testContainers

    val flags: Seq[ModuleID]      = Seq(
      "org.typelevel" %% "literally" % "1.1.0"
    ) ++ tests
    val httpClient: Seq[ModuleID] = http4sClient ++ http4s ++ tests
    val core: Seq[ModuleID]       =
        effect ++ json ++ tapir ++ http4sServer ++ model ++ commandLine ++ logging ++ observability ++ tests
}
