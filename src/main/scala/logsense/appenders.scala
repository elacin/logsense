package logsense

import cats.Semigroup
import scala.collection.mutable

abstract class Appender[I, O: Semigroup] { self =>
  final def xmap[II](f: II => I): Appender[II, O] =
    new Appender[II, O] {
      override def submit(e: Entry[II]): O =
        self.submit(e xmap f)
    }

  final def &&(other: Appender[I, O]): Appender[I, O] =
    new Appender[I, O]{
      override def submit(e: Entry[I]): O =
        Semigroup.combine(self.submit(e), other.submit(e))
    }

  def submit(e: Entry[I]): O
}

object appenders {
  import cats.std.unit._

  object NoopAppender extends Appender[String, Unit]{
    override def submit(i: Entry[String]): Unit =
      ()
  }

  object CachingAppender extends Appender[String, Unit]{
    var cache = mutable.ArrayBuffer.empty[Entry[String]]

    override def submit(i: Entry[String]): Unit =
      cache += i
  }

  final case class ConsoleCustom[I](f: Entry[I] => String) extends Appender[I, Unit]{
    override def submit(e: Entry[I]): Unit =
      println(f(e))
  }
}