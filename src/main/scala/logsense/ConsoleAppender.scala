package logsense

import java.io.File

object NoopAppender extends Appender[String, Unit] {
  override def apply(v1: Entry[String]): Unit = ()
}

final case class ConsoleAppender(f: Entry[String] => String) extends Appender[String, Unit] {
  override def apply(e: Entry[String]): Unit =
    println(f(e))
}

final case class ConsoleCustomAppender[I](f: Entry[I] => String) extends Appender[I, Unit] {
  override def apply(e: Entry[I]): Unit =
    println(f(e))
}

case class OutFilename(path: File, name: String, extension: String)

sealed trait RolloverPolicy
sealed trait TimeRollover extends RolloverPolicy
sealed trait SizeRollover extends RolloverPolicy

trait RollingFileAppender[I] extends Appender[I, Unit] {

}