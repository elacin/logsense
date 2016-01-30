package logsense

final class Filter[I] (val accepts: Entry[I] => FilterRes) {
  def xmap[II](f: II => I): Filter[II] =
    new Filter[II](e => accepts(e xmap f))

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
  def all[T] =
    Filter[T](_ => true)

  def hasException[T] =
    Filter[T](_.thOpt.isDefined)

  def inPackage[T](s: String) =
    Filter[T]{
      case Entry(_, _, SourceLocMacro(e, _, _), _, _) =>
        e.value startsWith s
      case Entry(_, _, SourceLocLogger(logger), _, _) =>
        logger startsWith s
    }

  def trace[T]      = Filter[T](_.level == Trace)
  def debug[T]      = Filter[T](_.level == Debug)
  def info[T]       = Filter[T](_.level == Info)
  def warning[T]    = Filter[T](_.level == Warning)
  def error[T]      = Filter[T](_.level == Error)

  def debugMin[T]   = Filter[T](_.level >= Debug)
  def infoMin[T]    = Filter[T](_.level >= Info)
  def warningMin[T] = Filter[T](_.level >= Warning)

  def warningMax[T] = Filter[T](_.level <= Warning)
  def infoMax[T]    = Filter[T](_.level <= Info)
  def debugMax[T]   = Filter[T](_.level <= Debug)
}