package pillars.flags

import io.circe.Codec

final case class FeatureFlagsConfig(
    enabled: Boolean = true,
    flags: List[FeatureFlag] = List.empty
) derives Codec.AsObject
