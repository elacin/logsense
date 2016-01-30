package logsense

sealed trait Lazy[T]{ self =>
  def value: T

  final def map[U](f: T => U): Lazy[U] =
    new Lazy[U] {
      override lazy val value: U = f(self.value)
    }
}

object Lazy {
  def apply[T](t: => T) = new Lazy[T] {
    override lazy val value: T = t
  }
}