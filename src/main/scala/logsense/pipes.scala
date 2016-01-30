package logsense

import java.time.LocalDateTime

import cats.Monoid

class Pipe[I, O: Monoid](val appender: Appender[I, O], val filter: Filter[I]){
  def copy(appender: Appender[I, O] = appender, filter: Filter[I] = filter): Pipe[I, O] =
    new Pipe(appender, filter)

  def xmap[II](f: II => I): Pipe[II, O] =
    new Pipe[II, O](appender xmap f, filter xmap f)

  def flush(level: Level,
            _i:    => I,
            thOpt: Option[Throwable],
            context: Map[String, String])
           (implicit loc:   SourceLocMacro): O = {

    val e =
      Entry(
        time     = LocalDateTime.now,
        level    = level,
        location = loc,
        input    = Lazy(_i),
        thOpt    = thOpt,
        context  = context
      )

    filter.accepts(e) match {
      case Take | Undecided => appender.submit(e)
      case Drop             => Monoid[O].empty
    }
  }
}

object Pipe {
  def apply[I, O: Monoid](a:  Appender[I, O],
                          f:  Filter[I],
                          fs: Filter[I]*): Pipe[I, O] =

    new Pipe[I, O](a, fs.foldLeft(f)(_ || _))

  def unfiltered[I, O: Monoid](a: Appender[I, O]): Pipe[I, O] =
    apply(a, filters.all)
}