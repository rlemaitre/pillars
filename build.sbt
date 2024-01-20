import org.typelevel.scalacoptions.{ScalaVersion, ScalacOptions}

name                             := "pillars"
ThisBuild / tlBaseVersion        := "0.0"
ThisBuild / homepage             := Some(url("https://pillars.rlemaitre.com/"))
ThisBuild / organization         := "com.rlemaitre"
ThisBuild / organizationName     := "Raphaël Lemaitre"
ThisBuild / organizationHomepage := Some(url("https://rlemaitre.com/"))
ThisBuild / startYear            := Some(2023)
ThisBuild / licenses             := Seq(License.Apache2)
ThisBuild / developers           ++= List(
    tlGitHubDev("rlemaitre", "Raphaël Lemaitre")
)
ThisBuild / tlSonatypeUseLegacyHost := false
ThisBuild / scalaVersion         := "3.3.1"

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


enablePlugins(ScalaUnidocPlugin, MergifyPlugin)

ThisBuild / mergifyPrRules := {
    val authorCondition = MergifyCondition.Or(
        List(
            MergifyCondition.Custom("author=scala-steward"),
            MergifyCondition.Custom("author=scala-steward-dev")
        )
    )
    Seq(
        MergifyPrRule(
            "label scala-steward's PRs",
            List(authorCondition),
            List(MergifyAction.Label(List("dependency-update")))
        ),
        MergifyPrRule(
            "merge scala-steward's PRs",
            List(authorCondition) ++ mergifySuccessConditions.value,
            List(MergifyAction.Merge(Some("merge")))
        )
    )
}

outputStrategy := Some(StdoutOutput)

lazy val core = Project("pillars-core", file("modules/core"))
    .settings(
      name := "pillars-core",
      libraryDependencies ++= Dependencies.core
    )

lazy val db = Project("pillars-db", file("modules/db"))
    .settings(
      name := "pillars-db",
      libraryDependencies ++= Dependencies.database
    )
    .dependsOn(core)

lazy val flags = Project("pillars-flags", file("modules/flags"))
    .settings(
      name := "pillars-flags",
      libraryDependencies ++= Dependencies.flags
    )
    .dependsOn(core)

lazy val httpClient = Project("pillars-http-client", file("modules/http-client"))
    .settings(
      name := "pillars-http-client",
      libraryDependencies ++= Dependencies.httpClient
    )
    .dependsOn(core)

lazy val example = Project("pillars-example", file("modules/example"))
    .settings(
      name := "pillars-example"
    )
    .enablePlugins(NoPublishPlugin)
    .dependsOn(core, db, flags)

lazy val docs = Project("pillars-docs", file("modules/docs"))
    .settings(
      name := "pillars-docs"
    )
    .dependsOn(core)

lazy val pillars = project
    .in(file("."))
    .aggregate(core, example, docs, db, flags, httpClient)
    .settings(
        name            := "pillars",
        publishArtifact := false,
        ScalaUnidoc / unidoc / unidocProjectFilter := inAnyProject -- inProjects(example, docs),
        ScalaUnidoc / unidoc / target := file("target/microsite/output/api"),
        ScalaUnidoc / unidoc / scalacOptions ++= Seq(
            "-project", name.value,
            "-project-version", version.value,
            "-project-logo", "modules/docs/src/docs/images/logo.png",
            //    "-source-links:github://rlemaitre/pillars",
            "-social-links:github::https://rlemaitre.github.io/pillars",
        )
    )
