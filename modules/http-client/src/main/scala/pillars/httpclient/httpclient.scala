package pillars.httpclient

import cats.effect.Async
import cats.effect.Resource
import cats.effect.std.Console
import cats.syntax.all.*
import fs2.io.net.Network
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import org.http4s.Request
import org.http4s.Response
import org.http4s.Uri
import org.http4s.client.Client
import org.http4s.netty.client.NettyClientBuilder
import org.typelevel.otel4s.trace.Tracer
import pillars.Module
import pillars.Modules
import pillars.Pillars
import pillars.PillarsError
import pillars.PillarsError.*
import sttp.tapir.AnyEndpoint
import sttp.tapir.DecodeResult
import sttp.tapir.Endpoint
import sttp.tapir.ValidationError
import sttp.tapir.client.http4s.Http4sClientInterpreter
import sttp.tapir.client.http4s.Http4sClientOptions

class Loader extends pillars.Loader:
    override type M[F[_]] = HttpClient[F]

    override def key: Module.Key = HttpClient.Key

    override def load[F[_]: Async: Network: Tracer: Console](
        context: pillars.Loader.Context[F],
        modules: Modules[F]
    ): Resource[F, HttpClient[F]] =
        NettyClientBuilder[F].withHttp2.withNioTransport.resource.map(HttpClient.apply)
end Loader

final case class HttpClient[F[_]: Async](client: org.http4s.client.Client[F])
    extends pillars.Module[F]:
    export client.*

    def call[SI, I, EO, O, R](
        endpoint: Endpoint[SI, I, EO, O, R],
        uri: Option[Uri],
        clientOptions: Http4sClientOptions = Http4sClientOptions.default
    )(securityInput: SI, input: I): F[Either[EO, O]] =
        val (request, parseResponse) =
            Http4sClientInterpreter[F](clientOptions).toSecureRequest(endpoint, uri)(securityInput)(input)
        import HttpClient.Error.*
        client
            .run(request)
            .use(parseResponse)
            .flatMap:
                case DecodeResult.Value(v)                   => v.pure[F]
                case DecodeResult.Error(raw, error)          =>
                    DecodingError(endpoint, uri, raw, error).raiseError[F, Either[EO, O]]
                case DecodeResult.Missing                    => Missing(endpoint, uri).raiseError[F, Either[EO, O]]
                case DecodeResult.Multiple(vs)               => Multiple(endpoint, uri, vs).raiseError[F, Either[EO, O]]
                case DecodeResult.Mismatch(expected, actual) =>
                    Mismatch(endpoint, uri, expected, actual).raiseError[F, Either[EO, O]]
                case DecodeResult.InvalidValue(errors)       =>
                    InvalidInput(endpoint, uri, errors).raiseError[F, Either[EO, O]]
    end call

end HttpClient

object HttpClient:
    def apply[F[_]](using p: Pillars[F]): Client[F] = p.module[HttpClient[F]](Key).client
    case object Key extends Module.Key:
        override def name: String = "http-client"

    enum Error(endpoint: AnyEndpoint, uri: Option[Uri], val number: ErrorNumber, val message: Message)
        extends PillarsError:
        case DecodingError(endpoint: AnyEndpoint, uri: Option[Uri], raw: String, cause: Throwable) extends Error(
              endpoint,
              uri,
              ErrorNumber(1001),
              Message(s"Cannot decode output $raw. Cause is $cause".assume)
            )
        case Missing(endpoint: AnyEndpoint, uri: Option[Uri])
            extends Error(endpoint, uri, ErrorNumber(1002), Message("Missing"))
        case Multiple[R](endpoint: AnyEndpoint, uri: Option[Uri], vs: Seq[R])
            extends Error(endpoint, uri, ErrorNumber(1003), Message("Multiple response"))
        case InvalidInput(endpoint: AnyEndpoint, uri: Option[Uri], errors: List[ValidationError[_]])
            extends Error(endpoint, uri, ErrorNumber(1004), Message("Invalid input"))
        case Mismatch(endpoint: AnyEndpoint, uri: Option[Uri], expected: String, actual: String)
            extends Error(endpoint, uri, ErrorNumber(1005), Message("Type mismatch"))

        override def code: Code = Code("HTTP")

        override def details: Option[Message] =
            Message.option(s"""
              |uri: $uri
              |endpoint: $endpoint
              |""")
    end Error
end HttpClient

private[httpclient] final case class Config(followRedirect: Boolean)
extension [F[_]](p: Pillars[F])
    def httpClient: Client[F] = p.module[HttpClient[F]](HttpClient.Key).client
