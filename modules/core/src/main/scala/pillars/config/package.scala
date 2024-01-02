package pillars

import cats.Show
import com.comcast.ip4s.Host
import com.comcast.ip4s.Port
import io.circe.Decoder
import io.circe.Encoder

import scala.concurrent.duration.FiniteDuration
import scala.jdk.DurationConverters.*

package object config:
  given Decoder[Host] = Decoder.decodeString.emap(t => Host.fromString(t).toRight("Failed to parse Host"))
  given Encoder[Host] = Encoder.encodeString.contramap(_.toString)

  given Decoder[Port] = Decoder.decodeInt.emap(t => Port.fromInt(t).toRight("Failed to parse Port"))
  given Encoder[Port] = Encoder.encodeInt.contramap(_.value)

  given Decoder[FiniteDuration] = Decoder.decodeDuration.map(_.toScala)
  given Encoder[FiniteDuration] = Encoder.encodeDuration.contramap(_.toJava)

  given [T: Decoder: Show]: Decoder[Secret[T]] = summon[Decoder[T]].map(Secret.apply)
  given [T: Encoder: Show]: Encoder[Secret[T]] = summon[Encoder[T]].contramap(_.value)
  
  given [T: Decoder: Show]: Decoder[Redacted[T]] = summon[Decoder[T]].map(Redacted.apply)
  given [T: Encoder: Show]: Encoder[Redacted[T]] = summon[Encoder[T]].contramap(_.value)
  