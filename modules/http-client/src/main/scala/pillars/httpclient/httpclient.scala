package pillars.httpclient

import cats.effect.Async
import cats.effect.Resource
import cats.effect.std.Console
import fs2.io.net.Network
import org.http4s.client.Client
import org.http4s.netty.client.NettyClientBuilder
import org.typelevel.otel4s.trace.Tracer
import pillars.Module
import pillars.Modules
import pillars.Pillars

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

end HttpClient

object HttpClient:
    def apply[F[_]](using p: Pillars[F]): Client[F] = p.module[HttpClient[F]](Key).client
    case object Key extends Module.Key:
        override def name: String = "http-client"
private[httpclient] final case class Config(followRedirect: Boolean)
extension [F[_]](p: Pillars[F])
    def httpClient: Client[F] = p.module[HttpClient[F]](HttpClient.Key).client
