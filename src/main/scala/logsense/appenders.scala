package logsense

import scala.collection.mutable

abstract class Appender[I] { self =>
  final def xmap[II](f: II => I): Appender[II] =
    new Appender[II] {
      override def submit(e: Entry[II]): Unit =
        self.submit(e xmap f)
    }

  final def &&(other: Appender[I]): Appender[I] =
    new Appender[I]{
      override def submit(e: Entry[I]): Unit = {
        self.submit(e)
        other.submit(e)
      }
    }

  def submit(e: Entry[I]): Unit
}

object appenders {

  object NoopAppender extends Appender[String]{
    override def submit(i: Entry[String]): Unit =
      ()
  }

  object CachingAppender extends Appender[String]{
    var cache = mutable.ArrayBuffer.empty[Entry[String]]

    override def submit(i: Entry[String]): Unit =
      cache += i
  }

  final case class ConsoleCustom[I](f: Entry[I] => String) extends Appender[I]{
    override def submit(e: Entry[I]): Unit =
      println(f(e))
  }
}