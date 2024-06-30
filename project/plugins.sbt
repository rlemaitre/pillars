addSbtPlugin("org.jetbrains.scala" % "sbt-ide-settings" % "1.1.2")
addSbtPlugin("org.typelevel"       % "sbt-tpolecat"     % "0.5.1")
addSbtPlugin("org.scalameta"       % "sbt-scalafmt"     % "2.5.2")

// Cross compilation
//addSbtPlugin("org.portable-scala" % "sbt-scala-native-crossproject" % "1.3.2")
//addSbtPlugin("org.scala-native"   % "sbt-scala-native"              % "0.4.16")

// documentation
addSbtPlugin("com.github.sbt" % "sbt-unidoc"    % "0.5.0")
addSbtPlugin("com.eed3si9n"   % "sbt-buildinfo" % "0.12.0")
addSbtPlugin("com.github.sbt" % "sbt-dynver"    % "5.0.1")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype"   % "3.11.0")
addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.5.12")

// Giter8 scaffold
addSbtPlugin("org.foundweekends.giter8" % "sbt-giter8-scaffold" % "0.16.2")
