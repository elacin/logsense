package logsense

import java.io.File

case class OutFilename(path: File, name: String, extension: String)

sealed trait RolloverPolicy
sealed trait TimeRollover extends RolloverPolicy
sealed trait SizeRollover extends RolloverPolicy

trait RollingFileAppender[I] extends Appender[I, Unit] {

}
