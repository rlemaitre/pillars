package pillars.config

import scodec.bits.ByteVector

final case class Secret[T](value: T) extends AnyVal:
  override def toString: String =
    val hash = ByteVector(value.hashCode).padRight(4).toHex.take(4)
    s"REDACTED-$hash"
