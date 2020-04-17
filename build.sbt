name := "2020HackathonProject"

version := "0.1"

scalaVersion := "2.12.8"

val http4sVersion = "0.21.2"
val circeVersion = "0.13.0"

lazy val root = (project in file("."))
  .settings(
    libraryDependencies ++= Seq (
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "org.http4s" %% "http4s-blaze-server" % http4sVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-literal" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "org.typelevel" %% "cats-effect" % "2.1.2"
    ),
    scalacOptions ++= Seq("-Ypartial-unification")
  )