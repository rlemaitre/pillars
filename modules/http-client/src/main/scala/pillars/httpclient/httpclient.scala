package pillars.httpclient

import cats.effect.Async
import cats.effect.Resource
import cats.effect.std.Console
import fs2.io.net.Network
import org.http4s.client.Client
import org.http4s.netty.client.NettyClientBuilder
import org.typelevel.otel4s.trace.Tracer
import pillars.Modules
import pillars.Pillars

class Loader extends pillars.Loader:
    override type M[F[_]] = HttpClient[F]
    override def name: String = "http-client"
    override def load[F[_]: Async: Network: Tracer: Console](
        context: pillars.Loader.Context[F],
        modules: Modules[F]
    ): Resource[F, HttpClient[F]] =
        NettyClientBuilder[F].withHttp2.withNioTransport.resource.map(HttpClient.apply)
end Loader

final case class HttpClient[F[_]: Async](nettyClient: org.http4s.client.Client[F])
    extends pillars.Module[F]

final case class Config(followRedirect: Boolean)
extension [F[_]: Async](pillars: Pillars[F])
    def httpClient: Client[F] = pillars.module[HttpClient[F]].nettyClient
