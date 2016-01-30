package logsense

object client extends App {
  import filters._
  import appenders._
  import cats.std.unit._


  val consoleAppender: Appender[String, Unit] = {
    def locStr(location: SourceLoc): String =
      location match {
        case SourceLocLogger(value) =>
          value
        case SourceLocMacro(enclosing, file, line) =>
          s"${enclosing.value} (${file.value.split("/").last}: ${line.value})"
      }

    def ctxStr(context: Map[String, String]): String = {
      if (context.isEmpty) ""
      else context.mkString("[", ",", "]: ")
    }

    ConsoleCustom {
      case Entry(t, level, loc: SourceLoc, msg, Some(th), ctx) =>
        s"$t: $level: ${locStr(loc)}: ${ctxStr(ctx)}${msg.lazyValue}: ${th.getMessage}"
      case Entry(t, level, loc, msg, None, ctx) =>
        s"$t: $level: ${locStr(loc)}: ${ctxStr(ctx)} ${msg.lazyValue}"
    }
  }
  val pipe: Pipe[String, Unit] =
    Pipe(
      consoleAppender && NoopAppender && CachingAppender,
      infoMin  && inPackage("logsense"),
      debugMin && hasException,
      warningMin
    )

  val ctx: Context[String, Unit] =
    ContextUnit(pipe)

  Lazy[String](???) map (_.toString)
  ctx.trace("ARNE", new RuntimeException("ex"))
  ctx.debug("ARNE")
  ctx.info("ARNE")
  ctx.warn("ARNE")
  ctx.warning("ARNE")
  ctx.error("ARNE")

  {
    case class Secret(hidden: String)

    val FromSecret: Secret => String =
      v => s"Discovered «${v.hidden}»!"

    val myCtx: Context[Secret, Unit] =
      (ctx
        xmap FromSecret
        filtered Filter(_.input.lazyValue.hidden.contains("xyzzy"))
        enriched ("userId" -> 113.toString)
      )

    myCtx.info(Secret("xyzzy"))
    myCtx.info(Secret("XYZZY"))
  }

  CachingAppender.cache foreach println
}