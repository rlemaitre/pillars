// https://typelevel.org/sbt-typelevel/faq.html#what-is-a-base-version-anyway
ThisBuild / tlBaseVersion := "0.0" // your current series x.y

name                         := "pillars"
ThisBuild / organization     := "com.rlemaitre"
ThisBuild / organizationName := "Raphaël Lemaitre"
ThisBuild / startYear        := Some(2023)
ThisBuild / licenses         := Seq(License.Apache2)
ThisBuild / developers := List(
  tlGitHubDev("rlemaitre", "Raphaël Lemaitre")
)

// publish to s01.oss.sonatype.org (set to true to publish to oss.sonatype.org instead)
ThisBuild / tlSonatypeUseLegacyHost := false

// publish website from this branch
ThisBuild / tlSitePublishBranch := Some("main")
ThisBuild / scalaVersion        := "3.3.1"

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

lazy val docs = Project("pillars-docs", file("site")).enablePlugins(TypelevelSitePlugin)

lazy val pillars = project
  .in(file("."))
  .settings(
    name            := "pillars",
    publishArtifact := false
  )
  .aggregate(core, example, docs)
