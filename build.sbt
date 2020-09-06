name := "smart-wallet-app"
organization in ThisBuild := "com.wsoczek"
scalaVersion in ThisBuild := "2.12.3"

// PROJECTS

lazy val global = project
  .in(file("."))
  .settings(settings)
  .disablePlugins(AssemblyPlugin)
  .aggregate(
    quotesAPI,
    smartWalletApp,
  )

lazy val quotesAPI = project
  .settings(
    name := "QuotesAPI",
    settings,
    libraryDependencies ++= commonDependencies
  )
  .disablePlugins(AssemblyPlugin)

lazy val smartWalletApp = project
  .settings(
    name := "SmartWalletApp",
    settings,
    assemblySettings,
    libraryDependencies ++= commonDependencies
  )
  .dependsOn(
    quotesAPI
  )

// DEPENDENCIES
lazy val dependencies =
  new {
    val slf4jVersion = "1.7.25"
    val typesafeConfigVersion = "1.3.1"
    val akkaVersion = "2.5.26"
    val akkaHttpVersion = "10.1.11"

    val slf4j = "org.slf4j" % "jcl-over-slf4j" % slf4jVersion
    val typesafeConfig = "com.typesafe" % "config" % typesafeConfigVersion
    val typesafeAkkaStream = "com.typesafe.akka" %% "akka-stream" % akkaVersion
    val typesafeAkkaHttp = "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
    val typesafeAkkaHttpSprayJson = "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion
  }

lazy val commonDependencies = Seq(
  dependencies.slf4j,
  dependencies.typesafeConfig,
  dependencies.typesafeAkkaStream,
  dependencies.typesafeAkkaHttp,
  dependencies.typesafeAkkaHttpSprayJson
)

// SETTINGS

lazy val settings =
  commonSettings ++
    wartremoverSettings ++
    scalafmtSettings

lazy val compilerOptions = Seq(
  "-unchecked",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-deprecation",
  "-encoding",
  "utf8"
)

lazy val commonSettings = Seq(
  scalacOptions ++= compilerOptions,
  resolvers ++= Seq(
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  )
)

lazy val wartremoverSettings = Seq(
  wartremoverWarnings in(Compile, compile) ++= Warts.allBut(Wart.Throw)
)

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true,
    scalafmtTestOnCompile := true,
    scalafmtVersion := "1.2.0"
  )

lazy val assemblySettings = Seq(
  assemblyJarName in assembly := name.value + ".jar",
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs@_*) => MergeStrategy.discard
    case "application.conf" => MergeStrategy.concat
    case x =>
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(x)
  }
)
