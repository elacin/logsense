package logsense

import java.time.LocalDateTime

import cats.{Eval, Later}

final case class Entry[I] private (
  time:     LocalDateTime,
  level:    Level,
  location: SourceLoc,
  input:    Eval[I],
  thOpt:    Option[Throwable],
  context:  Map[String, String]){

  def xmap[II](f: I => II): Entry[II] =
    Entry[II](time, level, location, input map f, thOpt, context)
}

object Entry{
  def apply[I](level:   Level,
               time:    LocalDateTime,
               _i:      => I,
               thOpt:   Option[Throwable],
               context: Map[String, String])
     (implicit loc:     SourceLocMacro): Entry[I] =

    Entry(
      time     = time,
      level    = level,
      location = loc,
      input    = Later(_i),
      thOpt    = thOpt,
      context  = context
    )
}