package logsense

import scala.language.implicitConversions
import scala.math.Ordering

sealed abstract class LogLevel(val value: Int){ self =>
  object orHigher extends UnApply {
    override def unapply(_2: LogLevel): Option[LogLevel] =
      if (self <= _2) Some(_2) else None
  }
}

case object Trace   extends LogLevel(1)
case object Debug   extends LogLevel(2)
case object Info    extends LogLevel(3)
case object Warning extends LogLevel(4)
case object Error   extends LogLevel(5)

trait UnApply {
  def unapply(_2: LogLevel): Option[LogLevel]
}

object LogLevel {

  /* make implicit syntax work with subtyping and without client-side import */
  implicit def infixOrderingOps(x: LogLevel): Ordering[LogLevel]#Ops =
    new ordering.Ops(x)

  implicit val ordering: Ordering[LogLevel] =
    Ordering.by[LogLevel, Int](_.value)
}
