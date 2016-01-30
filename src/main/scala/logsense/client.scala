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

  val appender: Appender[String, Unit] =
    ConsoleAppender {
      case Entry(t, level, loc: SourceLoc, msg, Some(th)) =>
        s"$t: $level: ${str(loc)}: ${msg.value}: ${th.getMessage}"
      case Entry(t, level, loc, msg, None) =>
        s"$t: $level: ${str(loc)}: ${msg.value}"
    }

  val f = FilterPartial[String] {
    case Entry(_, Info.orHigherB(), loc, _, _) => Take
    case Entry(_, level, loc, _, _) if level < Info  => Drop
  }

  val ctx: LogContext[String, Unit] =
    LogContextUnit(
      appender |+| NoopAppender,
      (filters.hasExceptions || f) && filters.Package("logsense")
    )

  ctx.trace("ARNE", new RuntimeException("ex"))
  ctx.debug("ARNE")
  ctx.info("ARNE")
  ctx.warn("ARNE")
  ctx.error("ARNE")

  import pprint.Config.Defaults._

  pprint.pprintln(List(Seq(Seq("mgg", "mgg", "lols"), Seq("mgg", "mgg")), Seq(Seq("ggx", "ggx"), Seq("ggx", "ggx", "wtfx"))))

}