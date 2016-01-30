package logsense

import algebra.Monoid

class Context[I, O: Monoid](pipes: Seq[Pipe[I, O]]) {
  sealed abstract class AppenderLevel(level: Level) {
    def apply(msg: => I)(implicit loc: SourceLocMacro): O =
      Reduce(pipes)(_ flush (level, msg, None))

    def apply(msg: => I, th: Throwable)(implicit loc: SourceLocMacro): O =
      Reduce(pipes)(_ flush (level, msg, Some(th)))
  }

  object error extends AppenderLevel(Error)
  object warn  extends AppenderLevel(Warning)
  object info  extends AppenderLevel(Info)
  object debug extends AppenderLevel(Debug)
  object trace extends AppenderLevel(Trace)

  @inline def warning = warn
}

object ContextUnit {
  def apply[I, O: Monoid](ps: Pipe[I, O]*): Context[I, O] =
    new Context[I, O](ps)
}
