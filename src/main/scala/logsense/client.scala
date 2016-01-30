package logsense

object client extends App {
  import Semigroup.SemigroupOps

  def str(location: SourceLoc): String =
    location match {
      case Logger(value) =>
        value
      case SourceLocMacro(enclosing, file, line) =>
        s"${enclosing.value} (${file.value.split("/").last}: ${line.value})"
    }

  val appender: Appender[String, Unit] = ConsoleAppender {
    case Entry(t, level, loc: SourceLoc, msg, Some(th)) =>
      s"$t: $level: ${str(loc)}: ${msg.value}: ${th.getMessage}"
    case Entry(t, level, loc, msg, None) =>
      s"$t: $level: ${str(loc)}: ${msg.value}"
  }

  val f1 = Filter[Any]{
    case e if e.thOpt.isDefined => Take
  }
  val filter = Filter[String] {
    case Entry(_, Info.orHigher(other), loc, _, _) => Take
    case Entry(_, level, loc, _, _) if level < Info  => Drop
  }

  val ctx = LogContext(appender |+| NoopAppender, filter)

  ctx.trace("ARNE", new RuntimeException("ex"))
  ctx.debug("ARNE")
  ctx.info("ARNE")
  ctx.warn("ARNE")
  ctx.error("ARNE")
}