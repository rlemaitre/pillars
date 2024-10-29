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
    testEncode(Duration.Inf, "infinity")
    testDecode("infinity", Duration.Inf)
    testEncode(Duration.Zero, "0")
    testDecode("0", Duration.Zero)
    testEncode(Duration.MinusInf, "-infinity")
    testDecode("-infinity", Duration.MinusInf)
    testEncode(Duration.Undefined, "undefined")
    testDecode("undefined", Duration.Undefined)

    testEncode(0.second, "0")
    testEncode(0.minute, "0")
    testEncode(0.hour, "0")
    testEncode(0.day, "0")
    testDecode("0", Duration.Zero)

    testEncode(1.second, "PT1S")
    testDecode("PT1S", 1.second)
    testEncode(1.minute, "PT1M")
    testDecode("PT1M", 1.minute)
    testEncode(1.hour, "PT1H")
    testDecode("PT1H", 1.hour)
    testEncode(1.day, "PT24H")
    testDecode("PT24H", 1.day)

    testEncode(2.day + 3.hour + 4.minute + 5.second, "PT51H4M5S")

    testDecode("PT72S", 72.seconds)
    testDecode("PT1M12S", 72.seconds)

    testDecode("PT66M", 66.minutes)
    testDecode("PT1H6M", 66.minutes)

    testDecodeFailure("1", "DecodingFailure at : Text '1' cannot be parsed to a Duration")
    testDecodeFailure("PT1D", "DecodingFailure at : Text 'PT1D' cannot be parsed to a Duration")

    def testEncode(input: Duration, expectedValue: String): Unit =
        test(s"encoding '$input''"):
            val encodedValue = input.asJson
            assertEquals(encodedValue, Json.fromString(expectedValue))

    def testDecode(input: String, expectedValue: Duration): Unit =
        test(s"decoding of '$input''"):
            assertEquals(Decoder[Duration].decodeJson(input.asJson), expectedValue.asRight)

    def testDecodeFailure(input: String, expectedError: String): Unit =
        test(s"fail encoding of '$input''"):
            val encodedValue = input.asJson
            assertEquals(Decoder[Duration].decodeJson(encodedValue).leftMap(_.getMessage), expectedError.asLeft)

end DurationCodecTest
