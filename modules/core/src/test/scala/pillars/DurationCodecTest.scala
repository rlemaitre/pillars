package pillars

import cats.syntax.either.*
import io.circe.Decoder
import io.circe.Json
import io.circe.syntax.*
import munit.FunSuite
import pillars.codec.given
import scala.concurrent.duration.Duration
import scala.concurrent.duration.DurationInt

class DurationCodecTest extends FunSuite:
    testEncoding(Duration.Inf, "infinity")
    testEncoding(Duration.Zero, "0")
    testEncoding(Duration.MinusInf, "-infinity")
    testEncoding(Duration.Undefined, "undefined")

    testEncoding(0.second, "0")
    testEncoding(0.minute, "0")
    testEncoding(0.hour, "0")
    testEncoding(0.day, "0")

    testEncoding(1.second, "PT1S")
    testEncoding(1.minute, "PT1M")
    testEncoding(1.hour, "PT1H")
    testEncoding(1.day, "PT24H")

    testEncoding(2.day + 3.hour + 4.minute + 5.second, "PT51H4M5S")

    def testEncoding(input: Duration, expectedValue: String): Unit =
        test(s"encoding / decoding of '$expectedValue''"):
            val encodedValue = input.asJson
            assertEquals(encodedValue, Json.fromString(expectedValue))
            assertEquals(Decoder[Duration].decodeJson(encodedValue), input.asRight)

end DurationCodecTest
