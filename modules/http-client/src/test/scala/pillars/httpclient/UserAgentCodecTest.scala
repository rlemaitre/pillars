package pillars.httpclient

import cats.syntax.either.*
import io.circe.Decoder
import io.circe.Json
import io.circe.syntax.*
import munit.FunSuite
import org.http4s.ProductComment
import org.http4s.ProductId
import org.http4s.headers.`User-Agent`
import pillars.httpclient.HttpClient.Config.given

class UserAgentCodecTest extends FunSuite:
    var ua: `User-Agent` = `User-Agent`(ProductId("pillars", None), ProductId("netty", None))
    var str: String      = "pillars netty"
    testEncode(ua, str)
    testDecode(str, ua)

    ua = `User-Agent`(ProductId("foo", Some("bar")), List(ProductId("foo")))
    str = "foo/bar foo"
    testEncode(ua, str)
    testDecode(str, ua)

    ua = `User-Agent`(
      ProductId("Mozilla", Some("5.0")),
      List(
        ProductComment("Android; Mobile; rv:30.0"),
        ProductId("Gecko", Some("30.0")),
        ProductId("Firefox", Some("30.0"))
      )
    )
    str = "Mozilla/5.0 (Android; Mobile; rv:30.0) Gecko/30.0 Firefox/30.0"
    testEncode(ua, str)
    testDecode(str, ua)

    private def testEncode(input: `User-Agent`, expectedValue: String): Unit =
        test(s"encoding '$input''"):
            val encodedValue = input.asJson
            assertEquals(encodedValue, Json.fromString(expectedValue))

    private def testDecode(input: String, expectedValue: `User-Agent`): Unit =
        test(s"decoding of '$input''"):
            assertEquals(Decoder[`User-Agent`].decodeJson(input.asJson), expectedValue.asRight)

end UserAgentCodecTest
