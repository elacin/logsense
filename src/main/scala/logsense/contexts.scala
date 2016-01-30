package logsense

import java.time.LocalDateTime

case class LogContext[I, O: Monoid](
  appender: Appender[I, O],
  filter:   Filter[I]){

  sealed abstract class AppenderLevel(level: LogLevel) {
    private def go(_i:    => I,
                   thOpt: Option[Throwable])
         (implicit loc:   SourceLocMacro): O = {

      val e =
        Entry(
          time     = LocalDateTime.now,
          level    = level,
          location = loc,
          input    = new Lazy(_i),
          thOpt    = thOpt
        )

      filter.accept(e) match {
        case Take    => appender(e)
        case Shorten => Monoid.mzero
        case Drop    => Monoid.mzero
      }
    }

    def apply(msg: I)(implicit loc: SourceLocMacro): O =
      go(msg, None)

    def apply(msg: I, th: Throwable)(implicit loc: SourceLocMacro): O =
      go(msg, Some(th))
  }

  object error extends AppenderLevel(Error)
  object warn  extends AppenderLevel(Warning)
  object info  extends AppenderLevel(Info)
  object debug extends AppenderLevel(Debug)
  object trace extends AppenderLevel(Trace)
}