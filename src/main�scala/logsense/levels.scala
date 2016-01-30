package logsense

import scala.language.implicitConversions
import scala.math.Ordering

sealed abstract class Level(val value: Int)

case object Trace   extends Level(1)
case object Debug   extends Level(2)
case object Info    extends Level(3)
case object Warning extends Level(4)
case object Error   extends Level(5)

object Level {
  implicit val ordering: Ordering[Level] =
    Ordering.by[Level, Int](_.value)

  /* make implicit syntax work with subtyping and without client-side import */
  implicit def LogLevelOrderingOps(x: Level): Ordering[Level]#Ops =
    new ordering.Ops(x)
}
