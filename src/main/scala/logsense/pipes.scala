package logsense

import java.time.LocalDateTime

import cats.Monoid

class Pipe[I, O: Monoid](appender: Appender[I, O], filter: Filter[I]){
  def flush(level: Level,
            _i:    => I,
            thOpt: Option[Throwable])
           (implicit loc:   SourceLocMacro): O = {

    val e =
      Entry(
        time     = LocalDateTime.now,
        level    = level,
        location = loc,
        input    = Lazy(_i),
        thOpt    = thOpt
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