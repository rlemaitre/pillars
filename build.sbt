import org.typelevel.scalacoptions.{ScalaVersion, ScalacOptions}

name                             := "pillars"
ThisBuild / organization         := "com.rlemaitre"
ThisBuild / organizationName     := "Raphaël Lemaitre"
ThisBuild / organizationHomepage := Some(url("https://rlemaitre.com/"))
ThisBuild / startYear            := Some(2023)
ThisBuild / licenses             := Seq(License.Apache2)
ThisBuild / developers           := List(
  Developer(
    id = "rlemaitre",
    name = "Raphaël Lemaitre",
    email = "raphael@rlemaitre.com",
    url = url("https://rlemaitre.com/")
  )
)
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

enablePlugins(ScalaUnidocPlugin)

outputStrategy := Some(StdoutOutput)

lazy val core = Project("pillars-core", file("modules/core"))
    .settings(
      name := "pillars-core",
      libraryDependencies ++= Dependencies.all
    )

lazy val example = Project("pillars-example", file("modules/example"))
    .settings(
      name := "pillars-example"
    )
    .dependsOn(core)

lazy val docs = Project("pillars-docs", file("modules/docs"))
    .settings(
      name := "pillars-docs"
    )
    .dependsOn(core)

lazy val pillars = project
    .in(file("."))
    .settings(
      name            := "pillars",
      publishArtifact := false
    )
    .aggregate(core, example, docs)
