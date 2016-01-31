package logsense

import scala.language.implicitConversions

case object Take      extends FilterRes
case object Undecided extends FilterRes
case object Drop      extends FilterRes

sealed trait FilterRes {
  def ||(that: FilterRes): FilterRes =
    (this, that) match {
      case (Undecided, Undecided) => Undecided
      case (Take,      _)         => Take
      case (_,         Take)      => Take
      case (Drop,      _)         => Drop
      case (_,         Drop)      => Drop
    }

  def &&(that: FilterRes): FilterRes =
    (this, that) match {
      case (Undecided, Undecided) => Undecided
      case (Drop,      _)         => Drop
      case (_,         Drop)      => Drop
      case (Take,      _)         => Take
      case (_,         Take)      => Take
    }
}

object FilterRes {
  implicit def upgrade(b: Boolean): FilterRes =
    if (b) Take else Drop
}
