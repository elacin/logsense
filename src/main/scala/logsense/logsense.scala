package object logsense {
  type Appender[I, +O] = (Entry[I] => O)
}

