package logsense

import java.time.LocalDateTime

import cats.Later

class Pipe[I](val appender: Appender[I],
              val filter:   Filter[I]){

  def copy(appender: Appender[I] = appender,
           filter:   Filter[I]      = filter): Pipe[I] =
    new Pipe(appender, filter)

  def xmap[II](f: II => I): Pipe[II] =
    new Pipe[II](appender xmap f, filter xmap f)

  def flush(level:   Level,
            _i:      => I,
            thOpt:   Option[Throwable],
            context: Map[String, String])
  (implicit loc:     SourceLocMacro): Unit = {

    val e =
      Entry(
        time     = LocalDateTime.now,
        level    = level,
        location = loc,
        input    = Later(_i),
        thOpt    = thOpt,
        context  = context
      )

    filter.accepts(e) match {
      case Take | Undecided => appender.submit(e)
      case Drop             => ()
    }
  }
}

object Pipe {
  def apply[I](a: Appender[I], fs: Filter[I]*): Pipe[I] =
    new Pipe[I](a, if (fs.isEmpty) filters.all else fs.reduce(_ || _))
}