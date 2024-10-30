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

    given Decoder[FiniteDuration] = Decoder.instance: hc =>
        Decoder.decodeString(hc).flatMap:
            case "0"   => Duration.Zero.asRight
            case other => Decoder.decodeDuration(hc).map(_.toScala)

    given Encoder[FiniteDuration] = Encoder.instance:
        case Duration.Zero => "0".asJson
        case other         => other.toJava.toString.asJson

    given Decoder[Duration.Infinite] = Decoder.decodeString.map:
        case "infinity"  => Duration.Inf
        case "-infinity" => Duration.MinusInf
        case "undefined" => Duration.Undefined

    given Encoder[Duration.Infinite] = Encoder.instance:
        case Duration.Inf                          => "infinity".asJson
        case Duration.MinusInf                     => "-infinity".asJson
        case other if other.eq(Duration.Undefined) => "undefined".asJson
        case other                                 => other.toString.asJson

    given Decoder[Duration] = Decoder[FiniteDuration].widen.or(Decoder[Duration.Infinite].widen)
    given Encoder[Duration] = Encoder.instance:
        case infinite: Duration.Infinite => infinite.asJson
        case finite: FiniteDuration      => finite.asJson

    given Configuration = Configuration.default.withKebabCaseMemberNames.withKebabCaseConstructorNames.withDefaults
end codec
