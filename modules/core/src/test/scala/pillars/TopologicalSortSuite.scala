// Copyright (c) 2024-2024 by Raphaël Lemaitre and Contributors
// This software is licensed under the Eclipse Public License v2.0 (EPL-2.0).
// For more information see LICENSE or https://opensource.org/license/epl-2-0

package pillars

import munit.FunSuite

class TopologicalSortSuite extends FunSuite:
    test("topologicalSort returns sorted list for acyclic graph"):
        val dependencies: Map[Char, Iterable[Char]] = Map(
          'A' -> List('D'),
          'B' -> List('D'),
          'C' -> List('A', 'B'),
          'D' -> List('E'),
          'E' -> List()
        )
        end dependencies
        assertEquals(dependencies.topologicalSort(identity).map(_.map(_._1)), Right(List('E', 'D', 'A', 'B', 'C')))

    test("topologicalSort returns error for cyclic graph"):
        val dependencies: Map[Char, Iterable[Char]] = Map(
          'A' -> List('B'),
          'B' -> List('C'),
          'C' -> List('A')
        )
        assertEquals(dependencies.topologicalSort(identity).map(_.map(_._1)), Left("Cyclic dependency found"))

    test("topologicalSort returns sorted list for single node graph"):
        val dependencies: Map[Char, Iterable[Char]] = Map('A' -> List())
        assertEquals(dependencies.topologicalSort(identity).map(_.map(_._1)), Right(List('A')))

    test("topologicalSort returns sorted list for disconnected graph"):
        val dependencies: Map[Char, Iterable[Char]] = Map('A' -> Nil, 'B' -> Nil, 'C' -> Nil, 'D' -> Nil, 'E' -> Nil)
        assertEquals(dependencies.topologicalSort(identity), Right(dependencies.toList))
end TopologicalSortSuite
