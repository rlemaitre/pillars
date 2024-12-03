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

extension [K, V](items: Map[K, V])
    def topologicalSort(dependencies: V => Iterable[K]): Either[String, List[(K, V)]] =
        @annotation.tailrec
        def loop(
            remaining: Map[K, V],
            sorted: List[(K, V)],
            visited: Set[K],
            recursionStack: Set[(K, V)]
        ): Either[String, List[(K, V)]] =
            if remaining.isEmpty then Right(sorted)
            else
                val (noDeps, hasDeps) = remaining.partition: (_, value) =>
                    dependencies(value).forall(visited.contains)
                if noDeps.isEmpty then
                    if hasDeps.exists(recursionStack.contains) then Left("Cyclic dependency found")
                    else loop(hasDeps, sorted, visited, recursionStack ++ hasDeps)
                else loop(hasDeps, sorted ++ noDeps.toList, visited ++ noDeps.keySet, recursionStack)

        loop(items, List.empty, Set.empty, Set.empty)
