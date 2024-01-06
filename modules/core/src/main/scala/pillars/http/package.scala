package pillars

import sttp.capabilities.fs2.Fs2Streams
import sttp.tapir.server.ServerEndpoint

package object http:
  type HttpEndpoint[F[_]] = ServerEndpoint[Fs2Streams[F], F]
