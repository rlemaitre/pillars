package pillars

import cats.syntax.all.*
import com.comcast.ip4s.Host
import com.comcast.ip4s.Port
import io.circe.Codec
import io.circe.Decoder
import io.circe.Encoder
import io.circe.derivation.Configuration
import org.http4s.Uri
import scala.concurrent.duration.FiniteDuration
import scala.jdk.DurationConverters.*

package object config:

    given Decoder[Host] = Decoder.decodeString.emap(t => Host.fromString(t).toRight("Failed to parse Host"))

    given Encoder[Host] = Encoder.encodeString.contramap(_.toString)

    given Decoder[Port] = Decoder.decodeInt.emap(t => Port.fromInt(t).toRight("Failed to parse Port"))

    given Encoder[Port] = Encoder.encodeInt.contramap(_.value)

    given Codec[Uri] = Codec.from(
      Decoder.decodeString.emap(t => Uri.fromString(t).leftMap(f => f.details)),
      Encoder.encodeString.contramap(_.toString)
    )

    given Decoder[FiniteDuration] = Decoder.decodeDuration.map(_.toScala)

    given Encoder[FiniteDuration] = Encoder.encodeDuration.contramap(_.toJava)

    given Configuration = Configuration.default.withKebabCaseMemberNames.withKebabCaseConstructorNames.withDefaults
end config
