package logsense

import sourcecode.{Line, File, Enclosing}

sealed trait SourceLoc

case class Logger(
  value: String
) extends SourceLoc

case class SourceLocMacro(
  enclosing: Enclosing,
  file: File,
  line: Line
) extends SourceLoc

object SourceLocMacro {
  implicit def provide(implicit _1: Enclosing, _2: File, _3: Line): SourceLocMacro =
    SourceLocMacro(_1, _2, _3)
}
