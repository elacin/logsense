package logsense

sealed trait YesNo
case object RetryYes extends YesNo
case object RetryNo  extends YesNo
