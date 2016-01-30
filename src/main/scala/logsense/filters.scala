package logsense

final class Filter[I] (val accepts: Entry[I] => FilterRes) {
  def &&(other: Filter[I]): Filter[I] =
    new Filter[I](e => accepts(e) && (other accepts e))

  def ||(other: Filter[I]): Filter[I] =
    new Filter[I](e => accepts(e) || (other accepts e))
}

object Filter {
  def apply[I](f: Function[Entry[I], Boolean]): Filter[I] =
    new Filter(f(_))
}

object FilterPartial {
  def apply[I](f: PartialFunction[Entry[I], FilterRes]): Filter[I] =
    new Filter(e => f lift e getOrElse Undecided)
}

object filters {
  def noop[T]: Filter[T] =
    Filter[T](_ => true)

  def hasExceptions[T]: Filter[T] =
    Filter[T](_.thOpt.isDefined)

  def `package`[T](s: String): Filter[T] =
    Filter[T]{
      case Entry(_, _, SourceLocMacro(e, _, _), _, _) => e.value startsWith s
      case Entry(_, _, Logger(logger), _, _)          => logger startsWith s
    }

  def trace     [T]: Filter[T] = Filter[T](_.level == Trace)
  def debug     [T]: Filter[T] = Filter[T](_.level == Debug)
  def info      [T]: Filter[T] = Filter[T](_.level == Info)
  def warning   [T]: Filter[T] = Filter[T](_.level == Warning)
  def error     [T]: Filter[T] = Filter[T](_.level == Error)

  def minDebug  [T]: Filter[T] = Filter[T](_.level >= Debug)
  def minInfo   [T]: Filter[T] = Filter[T](_.level >= Info)
  def minWarning[T]: Filter[T] = Filter[T](_.level >= Warning)

  def maxWarning[T]: Filter[T] = Filter[T](_.level <= Warning)
  def maxInfo   [T]: Filter[T] = Filter[T](_.level <= Info)
  def maxDebug  [T]: Filter[T] = Filter[T](_.level <= Debug)
}