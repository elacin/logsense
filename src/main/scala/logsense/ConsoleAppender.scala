package logsense

object NoopAppender extends Appender[String, Unit] {
  override def apply(v1: Entry[String]): Unit = ()
}

final case class ConsoleAppender(f: Entry[String] => String) extends Appender[String, Unit] {
  override def apply(e: Entry[String]): Unit =
    println(f(e))
}

final case class ConsoleCustomAppender[I](f: Entry[I] => String) extends Appender[I, Unit] {
  override def apply(e: Entry[I]): Unit =
    println(f(e))
}