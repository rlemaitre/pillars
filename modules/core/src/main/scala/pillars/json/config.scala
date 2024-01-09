package pillars.json

import io.circe.Codec
import io.circe.Decoder
import io.circe.Encoder
import io.circe.derivation.Configuration
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.given
import io.github.iltotore.iron.circe.given
import pillars.config.*
import pillars.json.flags.given

object config:
  given Configuration = Configuration.default.withKebabCaseMemberNames.withKebabCaseConstructorNames.withDefaults
  given Codec[FeatureFlagsConfig] = Codec.AsObject.derivedConfigured

  given Codec[HttpServerConfig] = Codec.AsObject.derivedConfigured

  given Codec[LogConfig] = Codec.AsObject.derivedConfigured

  given [T: Decoder]: Decoder[PillarsConfig[T]] = Decoder.derivedConfigured

  given [T: Encoder]: Encoder[PillarsConfig[T]] = Encoder.AsObject.derivedConfigured

  given Codec[ObservabilityConfig] = Codec.AsObject.derivedConfigured

  given Codec[AdminConfig] = Codec.AsObject.derivedConfigured

  given Codec[ApiConfig] = Codec.AsObject.derivedConfigured

  given Codec[DatabaseConfig] = Codec.AsObject.derivedConfigured
