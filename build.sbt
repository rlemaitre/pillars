import org.typelevel.scalacoptions.ScalacOptions
import org.typelevel.scalacoptions.ScalaVersion

ThisBuild / versionScheme := Some("semver-spec")
inThisBuild(
  List(
    scalaVersion           := "3.3.3",
    organization           := "com.rlemaitre",
    homepage               := Some(url("https://pillars.rlemaitre.com/")),
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    sonatypeRepository     := "https://s01.oss.sonatype.org/service/local",
    pgpPublicRing          := file("/tmp/public.asc"),
    pgpSecretRing          := file("/tmp/secret.asc"),
    pgpPassphrase          := sys.env.get("PGP_PASSWORD").map(_.toArray),
    scmInfo                := Some(
      ScmInfo(
        url("https://github.com/rlemaitre/pillars/"),
        "scm:git:git@github.com:rlemaitre/pillars.git"
      )
    ),
    developers             := List(
      Developer(
        "rlemaitre",
        "Raphaël Lemaitre",
        "raphael@rlemaitre.com",
        url("https://github.com/rlemaitre")
      )
    ),
    startYear              := Some(2023),
    licenses += ("Apache-2.0", url(
      "http://www.apache.org/licenses/LICENSE-2.0"
    )),
    scalacOptions ++= ScalacOptions.tokensForVersion(
      ScalaVersion.V3_3_0,
      Set(
        ScalacOptions.deprecation,
        ScalacOptions.feature,
        ScalacOptions.fatalWarnings,
        ScalacOptions.lint
      ) ++ ScalacOptions.privateWarnOptions ++ ScalacOptions.privateWarnUnusedOptions
    ) ++ Seq("-new-syntax")
  )
)
javaOptions += "-Dotel.java.global-autoconfigure.enabled=true"

Compile / scalacOptions ++= ScalacOptions.tokensForVersion(
  ScalaVersion.V3_3_0,
  Set(
    ScalacOptions.sourceFuture,
    ScalacOptions.deprecation,
    ScalacOptions.feature,
    ScalacOptions.fatalWarnings,
    ScalacOptions.lint
  ) ++ ScalacOptions.privateWarnOptions ++ ScalacOptions.privateWarnUnusedOptions
) ++ Seq("-new-syntax", "-Xmax-inlines=128")

enablePlugins(ScalaUnidocPlugin)

outputStrategy := Some(StdoutOutput)

lazy val core = Project("pillars-core", file("modules/core"))
    .enablePlugins(BuildInfoPlugin)
    .settings(
      name             := "pillars-core",
      description      := "pillars-core is a scala 3 library providing base services for writing backend applications",
      libraryDependencies ++= Dependencies.core,
      buildInfoKeys    := Seq[BuildInfoKey](name, version, description),
      buildInfoPackage := "pillars.build"
    )

lazy val db = Project("pillars-db", file("modules/db"))
    .enablePlugins(BuildInfoPlugin)
    .settings(
      name             := "pillars-db",
      description      := "pillars-db is a scala 3 library providing database services for writing backend applications",
      libraryDependencies ++= Dependencies.database,
      buildInfoKeys    := Seq[BuildInfoKey](name, version, description),
      buildInfoPackage := "pillars.db.build"
    )
    .dependsOn(core)

lazy val dbMigrations = Project("pillars-db-migration", file("modules/db-migration"))
    .enablePlugins(BuildInfoPlugin)
    .settings(
      name             := "pillars-db-migration",
      description      := "pillars-db is a scala 3 library providing database migrations",
      libraryDependencies ++= Dependencies.migrations,
      buildInfoKeys    := Seq[BuildInfoKey](name, version, description),
      buildInfoPackage := "pillars.db.migrations.build"
    )
    .dependsOn(core, db)

lazy val flags = Project("pillars-flags", file("modules/flags"))
    .enablePlugins(BuildInfoPlugin)
    .settings(
      name             := "pillars-flags",
      description      := "pillars-flag is a scala 3 library providing feature flag services for writing backend applications",
      libraryDependencies ++= Dependencies.flags,
      buildInfoKeys    := Seq[BuildInfoKey](name, version, description),
      buildInfoPackage := "pillars.flags.build"
    )
    .dependsOn(core)

lazy val httpClient = Project("pillars-http-client", file("modules/http-client"))
    .enablePlugins(BuildInfoPlugin)
    .settings(
      name             := "pillars-http-client",
      description      := "pillars-http-client is a scala 3 library providing http client services for writing backend applications",
      libraryDependencies ++= Dependencies.httpClient,
      buildInfoKeys    := Seq[BuildInfoKey](name, version, description),
      buildInfoPackage := "pillars.httpclient.build"
    )
    .dependsOn(core)

// tag::example[]
lazy val example = Project("pillars-example", file("modules/example"))
    .enablePlugins(BuildInfoPlugin) // //<1>
    .settings(
      name             := "pillars-example",                                            // //<2>
      description      := "pillars-example is an example of application using pillars", // //<3>
      libraryDependencies ++= Dependencies.tests,
      buildInfoKeys    := Seq[BuildInfoKey](name, version, description),                // //<4>
      buildInfoOptions := Seq(BuildInfoOption.Traits("pillars.BuildInfo")),             // //<5>
      buildInfoPackage := "example.build",                                              // //<6>
      publish / skip   := true
    )
    .dependsOn(core, db, flags, httpClient, dbMigrations)
// end::example[]
lazy val docs = Project("pillars-docs", file("modules/docs"))
    .settings(
      name           := "pillars-docs",
      publish / skip := true
    )
    .dependsOn(core)

lazy val pillars = project
    .in(file("."))
    .aggregate(core, example, docs, db, flags, httpClient)
    .settings(
      name                                       := "pillars",
      publishArtifact                            := false,
      ScalaUnidoc / unidoc / unidocProjectFilter := inAnyProject -- inProjects(example, docs),
      ScalaUnidoc / unidoc / target              := file("target/microsite/output/api"),
      ScalaUnidoc / unidoc / scalacOptions ++= Seq(
        "-project",
        name.value,
        "-project-version",
        version.value,
        "-project-logo",
        "modules/docs/src/docs/images/logo.png",
        //    "-source-links:github://rlemaitre/pillars",
        "-social-links:github::https://rlemaitre.github.io/pillars"
      )
    )
