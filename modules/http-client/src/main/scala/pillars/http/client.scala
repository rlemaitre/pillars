package pillars.http

import cats.effect.Async
import cats.effect.Resource
import cats.effect.std.Console
import fs2.io.net.Network
import org.http4s.client.Client
import org.http4s.netty.client.NettyClientBuilder
import org.typelevel.otel4s.trace.Tracer
import pillars.Pillars
import pillars.http.server.Controller
import pillars.probes.Probe

object client:
    class Loader extends pillars.Loader:
        override type M[F[_]] = HttpClient[F]
        override def name: String = "http-client"
        override def load[F[_]: Async: Network: Tracer: Console](context: pillars.Loader.Context[F])
            : Resource[F, HttpClient[F]] =
            NettyClientBuilder[F].withHttp2.withNioTransport.resource.map(HttpClient.apply)
    end Loader

    final case class HttpClient[F[_]: Async](nettyClient: org.http4s.client.Client[F])
        extends pillars.Module[F]:
        override def probes: List[Probe[F]]                = Nil
        override def adminControllers: List[Controller[F]] = Nil
        export nettyClient.*
    end HttpClient

    final case class Config(
        followRedirect: Boolean
    )
    extension [F[_]: Async](pillars: Pillars[F])
        def httpClient: Client[F] = pillars.module[HttpClient[F]].nettyClient

end client
