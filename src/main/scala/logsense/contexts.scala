package logsense

final class Context[I](pipes:   Seq[Pipe[I]],
                       context: Map[String, String]) {

  def copy(pipes:   Seq[Pipe[I]]        = pipes,
           context: Map[String, String] = context): Context[I] =
    new Context(pipes, context)

  def enriched(cs: (String, String)*): Context[I] =
    copy(context = context ++ cs)

  def including(f: Filter[I]): Context[I] =
    copy(pipes map (p => p.copy(filter = p.filter || f)))

  def filtered(f: Filter[I]): Context[I] =
    copy(pipes map (p => p.copy(filter = p.filter && f)))

  def xmap[II](f: II => I): Context[II] =
    new Context(pipes map (_ xmap f), context)

  sealed abstract class AppenderLevel(level: Level) {
    def apply(msg: => I)(implicit loc: SourceLocMacro): Unit =
      pipes foreach (_ flush (level, msg, None, context))

    def apply(msg: => I, th: Throwable)(implicit loc: SourceLocMacro): Unit =
      pipes foreach (_ flush (level, msg, Some(th), context))
  }

  object error extends AppenderLevel(Error)
  object warn  extends AppenderLevel(Warning)
  object info  extends AppenderLevel(Info)
  object debug extends AppenderLevel(Debug)
  object trace extends AppenderLevel(Trace)

  @inline def warning = warn
}

object ContextUnit {
  def apply[I](pipes: Pipe[I]*): Context[I] =
    new Context[I](pipes, Map.empty)
}
