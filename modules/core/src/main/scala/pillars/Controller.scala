// Copyright (c) 2024-2024 by Raphaël Lemaitre and Contributors
// This software is licensed under the Eclipse Public License v2.0 (EPL-2.0).
// For more information see LICENSE or https://opensource.org/license/epl-2-0

package pillars

import sttp.capabilities.fs2.Fs2Streams
import sttp.tapir.server.ServerEndpoint

type Controller[F[_]] = List[Controller.HttpEndpoint[F]]

object Controller:
    type HttpEndpoint[F[_]] = ServerEndpoint[Fs2Streams[F], F]
