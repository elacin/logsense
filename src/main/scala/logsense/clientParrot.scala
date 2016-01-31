package logsense

object clientParrot extends App {
  import appenders._
  import cats.std.unit._
  import filters._

  case class Output(value: String)

  object OutputAppender extends Appender[String, Output] {
    def locStr(location: SourceLoc): String =
      location match {
        case SourceLocLogger(value) =>
          value
        case SourceLocMacro(enclosing, file, line) =>
          s"${enclosing.value} (${file.value.split("/").last}: ${line.value})"
      }

    def ctxStr(context: Map[String, String]): String =
      if (context.isEmpty) ""
      else context.mkString("[", ",", "]: ")

    override def submit(e: Entry[String]): Output = {
      val out =
        e match {
          case Entry(t, level, loc: SourceLoc, msg, Some(th), ctx) =>
            s"$t: $level: ${locStr(loc)}: ${ctxStr(ctx)}${msg.lazyValue}: ${th.getMessage}"
          case Entry(t, level, loc, msg, None, ctx) =>
            s"$t: $level: ${locStr(loc)}: ${ctxStr(ctx)} ${msg.lazyValue}"
        }
      Output(out)
    }
  }
  val pipe: Pipe[String, Output] =
    Pipe(
      OutputAppender && NoopAppender && CachingAppender,
      infoMin  && inPackage("logsense"),
      debugMin && hasException,
      warningMin
    )

  val ctx: Context[String, Output] =
    ContextUnit(pipe)

  Lazy[String](???) map (_.toString)

  ctx.trace("ARNE", new RuntimeException("ex"))
  ctx.debug("ARNE")
  ctx.info("ARNE")
  ctx.warn("ARNE")
  ctx.warning("ARNE")
  ctx.error("ARNE")
}