package pillars.config

import io.circe.*
import io.circe.derivation.Configuration
import scala.concurrent.duration.*

final case class ProbeConfig(
    timeout: FiniteDuration = 5.seconds,
    interval: FiniteDuration = 10.seconds,
    failureCount: Int = 3
)

object ProbeConfig:
    given Configuration      = Configuration.default.withKebabCaseMemberNames.withKebabCaseConstructorNames.withDefaults
    given Codec[ProbeConfig] = Codec.AsObject.derivedConfigured
end ProbeConfig
