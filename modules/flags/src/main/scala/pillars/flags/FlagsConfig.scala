package pillars.flags

import io.circe.Codec

final case class FlagsConfig(
    enabled: Boolean = true,
    flags: List[FeatureFlag] = List.empty
) derives Codec.AsObject
