organization := "com.example"

name := "epoll_test"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.13.1"

assemblyMergeStrategy in assembly := {
  case "META-INF/NOTICE.txt"|"META-INF/LICENSE.txt" => MergeStrategy.concat
  case "META-INF/io.netty.versions.properties" => MergeStrategy.first
  case x => (assemblyMergeStrategy in assembly).value(x)
}

val nettyVersion = "4.1.48.Final"

libraryDependencies ++= Seq(
   "ws.unfiltered" %% "unfiltered-netty-server" % "0.10.0-M7",
   "io.netty" % "netty-buffer" % nettyVersion,
   "io.netty" % "netty-codec" % nettyVersion,
   "io.netty" % "netty-codec-http" % nettyVersion,
   "io.netty" % "netty-common" % nettyVersion,
   "io.netty" % "netty-handler" % nettyVersion,
   "io.netty" % "netty-transport" % nettyVersion,
   "io.netty" % "netty-transport-native-epoll" % nettyVersion classifier "linux-x86_64",
   "io.netty" % "netty-transport-native-kqueue" % nettyVersion classifier "osx-x86_64",
   "org.javassist" % "javassist" % "3.27.0-GA" % "runtime",
)

resolvers ++= Seq(
  "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
)

