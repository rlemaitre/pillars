package pillars.config

import cats.Show
import io.circe.Decoder
import io.circe.Encoder

final case class Redacted[T](value: T) extends AnyVal:
    override def toString: String =
        s"REDACTED"
object Redacted:
    given [T: Decoder: Show]: Decoder[Redacted[T]] = summon[Decoder[T]].map(Redacted.apply)
    given [T: Encoder: Show]: Encoder[Redacted[T]] = summon[Encoder[T]].contramap(_.value)
