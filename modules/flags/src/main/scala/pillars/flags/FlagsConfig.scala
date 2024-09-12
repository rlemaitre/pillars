package pillars.flags

import io.circe.Codec

final case class FlagsConfig(
    enabled: Boolean = true,
    flags: List[FeatureFlag] = List.empty
) extends pillars.Config derives Codec.AsObject
