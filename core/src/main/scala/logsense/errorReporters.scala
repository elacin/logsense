package logsense


/**
  * This delegates error handling for `logsense` to the client,
  *  as naturally when logging fails, it has no place to put data.
  *
  * Typically you put these potentially very grave errors in a place
  *  you know will be picked up by your monitoring
  */
trait ErrorReporter[I] {

  /**
    * Report an exception that has occurred in a filter, that
    *  is when deciding whether to output a given log `Entry`
    *  for a given `Appender`
    * @return whether to output the `Entry` anyway
    */
  def failedFilter(th: Throwable, e: Entry[I]): YesNo

  /**
    * Report an exception that has occurred while submitting
    * a log `Entry`.
    * @param appender
    * @param th
    * @param e
    */
  def failedSubmit(appender: Appender[I], attempt: Int, th: Throwable, e: Entry[I])
}

class StdErrErrorReporter[I] extends ErrorReporter[I]{
  override def failedFilter(th: Throwable, e: Entry[I]): YesNo = {
    System.err.println(s"Filter failed")
    th.printStackTrace(System.out)
    RetryNo
  }

  override def failedSubmit(appender: Appender[I], attempt: Int, th: Throwable, e: Entry[I]): Unit = {

  }
}