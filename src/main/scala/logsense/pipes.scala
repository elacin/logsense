package logsense

import scala.util.{Failure, Success, Try}

sealed trait SubmitRet
object SubmitRet {
  case object Submitted extends SubmitRet
  case object Reported extends SubmitRet
  case object Dropped extends SubmitRet
}

class Pipe[I](val appenders:    Seq[Appender[I]],
              val filter:       Filter[I],
              val errorHandler: ErrorReporter[I]){

  def copy(appenders:    Seq[Appender[I]] = appenders,
           filter:       Filter[I]        = filter,
           errorHandler: ErrorReporter[I] = errorHandler): Pipe[I] =
    new Pipe(appenders, filter, errorHandler)

  def xmap[II](f: II => I): Pipe[II] =
    new Pipe[II](
      appenders map (_ xmap f),
      filter xmap f,
      null
    )

  def flush(e: Entry[I]): Seq[SubmitRet] =
    Try(filter accepts e) match {
      case Success(Take | Undecided) =>
        submitAll(e)
      case Success(Drop) =>
        Seq(SubmitRet.Dropped)
      case Failure(th) =>
        errorHandler failedFilter (th, e) match {
          case RetryYes => submitAll(e)
          case RetryNo  => Seq(SubmitRet.Reported)
        }
    }

  private[logsense] final def submitAll(entry: Entry[I]): Seq[SubmitRet] =
    appenders map submitOne(entry, attempt = 1)

  private[logsense] final def submitOne(entry:    Entry[I],
                                        attempt:  Int)
                                       (appender: Appender[I]): SubmitRet =

    Try(appender submit entry) match {
      case Success(()) =>
        SubmitRet.Submitted

      case Failure(th) =>
        errorHandler failedSubmit (appender, attempt, th, entry)

        appender shouldRetry (attempt, entry, th) match {
          case RetryYes if attempt < MaxAttempts =>
            submitOne(entry, attempt + 1)(appender)
          case _  =>
            SubmitRet.Reported
        }
    }
}

object Pipe {
  def apply[I](as: Appender[I]*)(fs: Filter[I]*): Pipe[I] =
    new Pipe[I](
      as,
      if (fs.isEmpty) filters.all else fs.reduce(_ || _),
      null
    )
}