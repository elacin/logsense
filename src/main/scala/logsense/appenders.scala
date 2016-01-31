package logsense

import scala.collection.mutable

abstract class Appender[I] { self =>
  final def xmap[II](f: II => I): Appender[II] =
    new Appender[II] {
      override def submit(e: Entry[II]): Unit =
        self.submit(e xmap f)
    }

  def shouldRetry(attempt: Int, entry: Entry[I], th: Throwable): YesNo =
    RetryNo

  def submit(e: Entry[I]): Unit
}

object appenders {
  object NoopAppender extends Appender[String]{
    override def submit(i: Entry[String]): Unit =
      ()
  }

  final case class ConsoleAppender[I](f: Entry[I] => String) extends Appender[I]{
    override def submit(e: Entry[I]): Unit =
      println(f(e))
  }

  object CachingAppender extends Appender[String]{
    var cache = mutable.ArrayBuffer.empty[Entry[String]]

    override def submit(i: Entry[String]): Unit =
      cache += i
  }
}