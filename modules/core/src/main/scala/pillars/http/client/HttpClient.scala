package pillars.http.client

import cats.effect.Resource
import cats.effect.kernel.Async
import org.http4s.client.Client
import org.http4s.netty.client.NettyClientBuilder

object HttpClient:
  def build[F[_]: Async](): Resource[F, Client[F]] =
    NettyClientBuilder[F].withHttp2.withNioTransport.resource
