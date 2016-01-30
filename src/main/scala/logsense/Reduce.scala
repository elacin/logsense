package logsense

import algebra.Monoid
import cats.syntax.group._

object Reduce {
  def apply[T, U: Monoid](s: Seq[T])(f: T => U): U =
    s.foldLeft(Monoid[U].empty)(_ |+| f(_))
}

