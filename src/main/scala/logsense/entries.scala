package logsense

import java.time.LocalDateTime

final case class Entry[I](
  time:     LocalDateTime,
  level:    Level,
  location: SourceLoc,
  input:    Lazy[I],
  thOpt:    Option[Throwable],
  context:  Map[String, String]){

  def xmap[II](f: I => II): Entry[II] =
    Entry[II](time, level, location, input map f, thOpt, context)
}