package pillars.http.server

import pillars.http.server.Controller.HttpEndpoint
import sttp.capabilities.fs2.Fs2Streams
import sttp.tapir.server.ServerEndpoint

trait Controller[F[_]]:
    def endpoints: List[HttpEndpoint[F]]

object Controller:
    type HttpEndpoint[F[_]] = ServerEndpoint[Fs2Streams[F], F]
