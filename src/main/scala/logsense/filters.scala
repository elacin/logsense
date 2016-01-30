package logsense

sealed trait FilterRes
object Take extends FilterRes
object Drop extends FilterRes
object Shorten extends FilterRes

class Filter[I](private val acceptOpt: Entry[I] => Option[FilterRes]) {
  final def accept(e: Entry[I]): FilterRes =
    acceptOpt(e) getOrElse Take
}

object Filter {
  def apply[I](f: PartialFunction[Entry[I], FilterRes]): Filter[I] =
    new Filter(f.lift)

  implicit def FilterSemigroup[I]: Semigroup[Filter[I]] =
    Semigroup.instance[Filter[I]]((t1, t2) =>
      new Filter(e => t1 acceptOpt e orElse (t2 acceptOpt e))
    )
}
