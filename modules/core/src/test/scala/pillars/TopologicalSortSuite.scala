// Copyright (c) 2024-2024 by Raphaël Lemaitre and Contributors
// This software is licensed under the Eclipse Public License v2.0 (EPL-2.0).
// For more information see LICENSE or https://opensource.org/license/epl-2-0

package pillars

import munit.FunSuite

class TopologicalSortSuite extends FunSuite:
    import graph.*
    test("topologicalSort returns sorted list for acyclic graph defined as List"):
        val items: List[Char] = List('A', 'B', 'C', 'D', 'E')
        assertEquals(
          items
              .topologicalSort:
                  case 'A' => List('D')
                  case 'B' => List('D')
                  case 'C' => List('A', 'B')
                  case 'D' => List('E')
                  case 'E' => List()
                  case _   => ???
          ,
          Right(List('E', 'D', 'A', 'B', 'C'))
        )

    test("topologicalSort returns error for cyclic graph"):
        val items = List('A', 'B', 'C')
        assertEquals(
          items
              .topologicalSort:
                  case 'A' => List('B')
                  case 'B' => List('C')
                  case 'C' => List('A')
                  case _   => ???
          ,
          Left(GraphError.CyclicDependencyError)
        )

    test("topologicalSort returns error if a dependency is missing"):
        val items = List('A', 'C')
        assertEquals(
          items
              .topologicalSort:
                  case 'A' => List('B')
                  case 'C' => List('A')
                  case _   => ???
          ,
          Left(GraphError.MissingDependency(Set('B')))
        )

    test("topologicalSort returns sorted list for single node graph"):
        val items = List('A')
        assertEquals(items.topologicalSort(_ => Nil), Right(List('A')))

    test("topologicalSort returns sorted list for disconnected graph"):
        val items = List('A', 'B', 'C', 'D', 'E')
        assertEquals(items.topologicalSort(_ => Nil), Right(items.toList))

end TopologicalSortSuite
