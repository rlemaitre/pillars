package pillars

import sttp.capabilities.fs2.Fs2Streams
import sttp.tapir.server.ServerEndpoint

type Controller[F[_]] = List[Controller.HttpEndpoint[F]]

object Controller:
    type HttpEndpoint[F[_]] = ServerEndpoint[Fs2Streams[F], F]
