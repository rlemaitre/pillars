package pillars.config

import io.circe.Codec
import pillars.json.flags.given
import pillars.model.FeatureFlag

final case class FeatureFlagsConfig(
    enabled: Boolean = true,
    flags: List[FeatureFlag] = List.empty
) derives Codec.AsObject
