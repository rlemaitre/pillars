// Copyright (c) 2024-2024 by Raphaël Lemaitre and Contributors
// This software is licensed under the Eclipse Public License v2.0 (EPL-2.0).
// For more information see LICENSE or https://opensource.org/license/epl-2-0

package pillars

import cats.data.Validated
import cats.effect.IO
import com.monovore.decline.Argument
import fs2.io.file.Path

given Argument[Path] with
    def read(string: String) = Validated.valid(Path(string))

    def defaultMetavar = "path"

/**
 * Type alias for a Pillars[F] context bound.
 *
 * @tparam F The effect type.
 * @tparam A The type of the value that is being computed.
 */
type Run[F[_], A] = Pillars[F] ?=> A

type RunIO[A] = Run[IO, A]
