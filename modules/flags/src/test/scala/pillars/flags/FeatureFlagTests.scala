package pillars.flags

import io.circe.Decoder
import io.circe.Decoder.Result
import io.circe.Json
import io.circe.syntax.*
import munit.CatsEffectSuite

class FeatureFlagTests extends CatsEffectSuite:

    test("Encoder should encode Enabled status to 'enabled' string"):
        val status  = FeatureFlag.Status.Enabled
        val encoded = status.asJson
        assertEquals(encoded, "enabled".asJson)

    test("Encoder should encode Disabled status to 'disabled' string"):
        val status  = FeatureFlag.Status.Disabled
        val encoded = status.asJson
        assertEquals(encoded, "disabled".asJson)

    test("Decoder should decode 'enabled' string to Enabled status"):
        val status  = "enabled"
        val decoded = Json.fromString(status).as[FeatureFlag.Status]
        assertEquals(decoded, Right(FeatureFlag.Status.Enabled))

    test("Decoder should decode 'disabled' string to Disabled status"):
        val status  = "disabled"
        val decoded = Json.fromString(status).as[FeatureFlag.Status]
        assertEquals(decoded, Right(FeatureFlag.Status.Disabled))

    test("Decoder should fail on invalid status string"):
        val status                              = "invalid"
        val decoded: Result[FeatureFlag.Status] = Json.fromString(status).as[FeatureFlag.Status]
        assertEquals(decoded.isLeft, true)
end FeatureFlagTests
