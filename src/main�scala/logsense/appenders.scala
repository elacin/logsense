package logsense

import java.io.File

import algebra.Semigroup

trait Appender[I, O] { self =>
  final def xmap[II](f: II => I): Appender[II, O] =
    new Appender[II, O] {
      override def submit(e: Entry[II]): O =
        self.submit(e map f)
    }

  def submit(e: Entry[I]): O
}

object Appender {
  implicit def AppenderSemiGroup[I, O: Semigroup]: Semigroup[Appender[I, O]] =
    new Semigroup[Appender[I, O]]{
      override def combine(a1: Appender[I, O], a2: Appender[I, O]): Appender[I, O] =
        new Appender[I, O]{
          override def submit(e: Entry[I]): O =
            Semigroup.combine(a1.submit(e), a2.submit(e))
        }
    }
}

object appenders {
  object NoopAppender extends Appender[String, Unit]{
    override def submit(i: Entry[String]): Unit = ()
  }

  case class ConsoleString[T](f: Entry[String] => String) extends Appender[String, Unit]{
    override def submit(e: Entry[String]): Unit =
      println(f(e))
  }

  case class ConsoleCustom[I](f: Entry[I] => String) extends Appender[I, Unit]{
    override def submit(e: Entry[I]): Unit =
      println(f(e))
  }
}