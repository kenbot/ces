scalaVersion := "2.10.4"

resolvers ++= Seq(
  "Sonatype OSS Releases" at "http://oss.sonatype.org/content/repositories/releases/",
  "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
)

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % "2.10.2")

initialCommands += """
  import kenbot.ces._;
  val es = Game.entities;
  val player = es('player)
"""

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-swing" % "2.10.4",
  "org.scalaz" %% "scalaz-core" % "7.0.3",
  "com.chuusai" % "shapeless_2.10.4" % "2.0.0")

EclipseKeys.withSource := true

fork := true

