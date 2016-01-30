package logsense

trait Semigroup[T] {
  def mappend(t1: T, t2: T): T
}

object Semigroup {
  implicit class SemigroupOps[T: Semigroup](t1: T){
    def |+|(t2: T): T =
      mappend(t1, t2)
  }

  implicit val unitSemiGroup: Semigroup[Unit] =
    instance[Unit]((_, _) => ())

  implicit def f0SemiGroup[O: Semigroup]: Semigroup[() => O] =
    instance[() => O]((f1, f2) => () => f1() |+| f2())

  implicit def f1SemiGroup[I, O: Semigroup]: Semigroup[I => O] =
    instance[I => O]((f1, f2) => (i: I) => f1(i) |+| f2(i))

  def instance[T](f: (T, T) => T): Semigroup[T] =
    new Semigroup[T] {
      override def mappend(t1: T, t2: T): T =
        f(t1, t2)
    }

  def mappend[T: Semigroup](t1: T, t2: T): T =
    implicitly[Semigroup[T]].mappend(t1, t2)
}

trait Monoid[T] extends Semigroup[T] {
  def mzero: T
}

object Monoid {
  implicit val unitMonoid: Monoid[Unit] =
    instance[Unit](())

  def mzero[T: Monoid]: T =
    implicitly[Monoid[T]].mzero

  def instance[T: Semigroup](_mzero: T): Monoid[T] =
    new Monoid[T] {
      override def mzero: T =
        _mzero
      override def mappend(t1: T, t2: T): T =
        Semigroup.mappend(t1, t2)
    }
}