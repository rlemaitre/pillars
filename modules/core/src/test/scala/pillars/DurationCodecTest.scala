package pillars

import cats.syntax.either.*
import io.circe.Decoder
import io.circe.Json
import io.circe.syntax.*
import munit.FunSuite
import pillars.codec.given
import scala.concurrent.duration.Duration

class DurationCodecTest extends FunSuite:
    testEncoding(Duration.Inf, "infinity")
    testEncoding(Duration.Zero, "0")
    testEncoding(Duration.MinusInf, "-infinity")
    testEncoding(Duration.Undefined, "undefined")

    def testEncoding[D <: Duration](input: D, expectedValue: String): Unit =
        test(s"encoding / decoding of '$expectedValue''"):
            val encodedValue = input.asJson
            assertEquals(encodedValue, Json.fromString(expectedValue))
            assertEquals(Decoder[Duration].decodeJson(encodedValue), input.asRight)

end DurationCodecTest
