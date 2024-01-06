package pillars

import cats.effect.IO
import sttp.capabilities.fs2.Fs2Streams
import sttp.tapir.server.ServerEndpoint

package object http:
  type HttpEndpoint = ServerEndpoint[Fs2Streams[IO], IO]
