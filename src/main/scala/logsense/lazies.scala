package logsense

sealed trait Lazy[T]{ self =>
  def lazyValue: T

  final def map[U](f: T => U): Lazy[U] =
    new Lazy[U] {
      override lazy val lazyValue: U = f(self.lazyValue)
    }
}

object Lazy {
  def apply[T](t: => T) = new Lazy[T] {
    override lazy val lazyValue: T = t
  }
}