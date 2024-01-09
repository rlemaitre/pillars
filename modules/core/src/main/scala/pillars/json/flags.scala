package pillars.json

import io.circe.Codec
import io.circe.Decoder
import io.circe.Encoder
import io.github.iltotore.iron.*
import io.github.iltotore.iron.circe.given
import pillars.model.FeatureFlag
import sttp.tapir.Schema
import sttp.tapir.codec.iron.*
import sttp.tapir.codec.iron.given

object flags:
  given Encoder[FeatureFlag.Status] = Encoder.encodeString.contramap:
    case FeatureFlag.Status.Enabled  => "enabled"
    case FeatureFlag.Status.Disabled => "disabled"

  given Decoder[FeatureFlag.Status] = Decoder.decodeString.emap:
    case "enabled"  => Right(FeatureFlag.Status.Enabled)
    case "disabled" => Right(FeatureFlag.Status.Disabled)
    case other      => Left(s"Invalid status $other")

  given Schema[FeatureFlag.Status] = Schema.derived
  given Codec[FeatureFlag]         = Codec.AsObject.derived
  given Schema[FeatureFlag]        = Schema.derived
