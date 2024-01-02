package pillars.config

final case class Redacted[T](value: T) extends AnyVal:
  override def toString: String =
    s"REDACTED"
