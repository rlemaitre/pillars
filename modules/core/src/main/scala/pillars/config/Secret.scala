package pillars.config

import io.circe.Decoder
import io.circe.Encoder
import scodec.bits.ByteVector

final case class Secret[T](value: T) extends AnyVal:
    override def toString: String =
        val hash = ByteVector(value.hashCode).padRight(4).toHex.take(4)
        s"REDACTED-$hash"
end Secret

object Secret:
    given [T: Decoder]: Decoder[Secret[T]] = summon[Decoder[T]].map(Secret.apply)
    given [T: Encoder]: Encoder[Secret[T]] = summon[Encoder[T]].contramap(_.value)
