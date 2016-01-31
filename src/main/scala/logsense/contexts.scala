package logsense

import algebra.Monoid

final class Context[I, O: Monoid](pipes:   Seq[Pipe[I, O]],
                                  context: Map[String, String]) {

  def copy(pipes:   Seq[Pipe[I, O]]     = pipes,
           context: Map[String, String] = context): Context[I, O] =
    new Context(pipes, context)

  def enriched(cs: (String, String)*): Context[I, O] =
    copy(context = context ++ cs)

  def including(f: Filter[I]): Context[I, O] =
    copy(pipes map (p => p.copy(filter = p.filter || f)))

  def filtered(f: Filter[I]): Context[I, O] =
    copy(pipes map (p => p.copy(filter = p.filter && f)))

  def xmap[II](f: II => I): Context[II, O] =
    new Context(pipes map (_ xmap f), context)

  sealed abstract class AppenderLevel(level: Level) {
    def apply(msg: => I)(implicit loc: SourceLocMacro): O =
      Reduce(pipes)(_ flush (level, msg, None, context))

    def apply(msg: => I, th: Throwable)(implicit loc: SourceLocMacro): O =
      Reduce(pipes)(_ flush (level, msg, Some(th), context))
  }

  object error extends AppenderLevel(Error)
  object warn  extends AppenderLevel(Warning)
  object info  extends AppenderLevel(Info)
  object debug extends AppenderLevel(Debug)
  object trace extends AppenderLevel(Trace)

  @inline def warning = warn
}

object ContextUnit {
  def apply[I, O: Monoid](pipes: Pipe[I, O]*): Context[I, O] =
    new Context[I, O](pipes, Map.empty)
}
