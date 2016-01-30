package logsense

import java.time.LocalDateTime

class Lazy[T](_value: => T) {
  lazy val value: T = _value
}

final case class Entry[I](
  time:     LocalDateTime,
  level:    LogLevel,
  location: SourceLoc,
  input:    Lazy[I],
  thOpt:    Option[Throwable]
)