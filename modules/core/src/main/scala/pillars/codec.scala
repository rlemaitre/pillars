package pillars

import cats.syntax.all.*
import com.comcast.ip4s.Host
import com.comcast.ip4s.Port
import fs2.io.file.Path
import io.circe.Codec
import io.circe.Decoder
import io.circe.Encoder
import io.circe.derivation.Configuration
import io.circe.syntax.*
import org.http4s.Uri
import scala.concurrent.duration.Duration
import scala.concurrent.duration.FiniteDuration
import scala.jdk.DurationConverters.*

object codec:

    given Decoder[Path] = Decoder.decodeString.emap(t => Right(Path(t)))

    given Encoder[Path] = Encoder.encodeString.contramap(_.toString)

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

    given Decoder[Duration] = Decoder.decodeDuration.map(_.toScala)
    given Encoder[Duration] = Encoder.instance[Duration]:
        case Duration.Inf       => "infinity".asJson
        case Duration.MinusInf  => "-infinity".asJson
        case Duration.Zero      => "0".asJson
        case Duration.Undefined => "undefined".asJson
        case d: FiniteDuration  => d.asJson
        case other              => other.toString.asJson

    given Configuration = Configuration.default.withKebabCaseMemberNames.withKebabCaseConstructorNames.withDefaults
end codec
