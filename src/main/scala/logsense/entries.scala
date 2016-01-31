package logsense

import java.time.LocalDateTime

import cats.{Eval, Later}

final case class Entry[I](
  time:     LocalDateTime,
  level:    Level,
  location: SourceLoc,
  input:    Eval[I],
  thOpt:    Option[Throwable],
  context:  Map[String, String]){

  def xmap[II](f: I => II): Entry[II] =
    Entry[II](time, level, location, input map f, thOpt, context)
}