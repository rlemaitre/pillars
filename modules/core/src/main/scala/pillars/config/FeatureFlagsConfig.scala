package pillars.config

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import pillars.model.FeatureFlag
final case class FeatureFlagsConfig(
    enabled: Boolean = true,
    flags: List[FeatureFlag] = List.empty
)
