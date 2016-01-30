package logsense

object client extends App {
  import filters._
  import appenders._
  import cats.std.unit._
  import cats.syntax.group._

  def str(location: SourceLoc): String =
    location match {
      case SourceLocLogger(value) =>
        value
      case SourceLocMacro(enclosing, file, line) =>
        s"${enclosing.value} (${file.value.split("/").last}: ${line.value})"
    }

  val consoleAppender: Appender[String, Unit] =
    ConsoleString {
      case Entry(t, level, loc: SourceLoc, msg, Some(th)) =>
        s"$t: $level: ${str(loc)}: ${msg.value}: ${th.getMessage}"
      case Entry(t, level, loc, msg, None) =>
        s"$t: $level: ${str(loc)}: ${msg.value}"
    }

  val pipe: Pipe[String, Unit] =
    Pipe(
      consoleAppender |+| NoopAppender,
      infoMin  && inPackage("logsense"),
      debugMin && hasException,
      warningMin
    )

  val ctx: Context[String, Unit] =
    ContextUnit(pipe)

  ctx.trace("ARNE", new RuntimeException("ex"))
  ctx.debug("ARNE")
  ctx.info("ARNE")
  ctx.warn("ARNE")
  ctx.warning("ARNE")
  ctx.error("ARNE")
  Lazy[Arne](???) map (_.toString)

  case class Arne(value: String)

  val arnePipe: Pipe[Arne, Unit] =
    Pipe.unfiltered(
      consoleAppender.xmap[Arne](_.value) |+| NoopAppender.xmap[Arne](_.value)
    )
  val arneCtx: Context[Arne, Unit] =
    ContextUnit(arnePipe)
  arneCtx.info(Arne("HALLOOO"))
}