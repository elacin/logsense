package logsense

import cats.Later

object client extends App {
  import filters._
  import appenders._

  val consoleAppender: Appender[String] = {
    def locStr(location: SourceLoc): String =
      location match {
        case SourceLocLogger(value) =>
          value
        case SourceLocMacro(enclosing, file, line) =>
          s"${enclosing.value} (${file.value.split("/").last}: ${line.value})"
      }

    def mapStr(context: Map[String, String]): String =
      if (context.isEmpty) "" else context.mkString("[", ",", "]: ")

    ConsoleAppender {
      case Entry(t, level, loc: SourceLoc, msg, Some(th), map) =>
        s"$t: $level: ${locStr(loc)}: ${mapStr(map)}${msg.value}: ${th.getMessage}"
      case Entry(t, level, loc, msg, None, map) =>
        s"$t: $level: ${locStr(loc)}: ${mapStr(map)} ${msg.value}"
    }
  }

  val pipe: Pipe[String] =
    Pipe(consoleAppender, NoopAppender, CachingAppender)(
      infoMin  && inPackage("logsense"),
      debugMin && hasException,
      warningMin
    )

  val ctx: Context[String] =
    ContextUnit(pipe)

  Later[String](???) map (_.toString)
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

    val secretLogger: Context[Secret] =
      (ctx
        xmap FromSecret
        filtered Filter(_.input.value.hidden.contains("xyzzy"))
        enriched ("userId" -> 113.toString)
      )

    secretLogger.info(Secret("xyzzy")) // printed
    secretLogger.info(Secret("xxzzy")) // not printed
  }

  CachingAppender.cache foreach println
}