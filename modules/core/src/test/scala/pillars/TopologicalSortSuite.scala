package pillars

import munit.FunSuite

class TopologicalSortSuite extends FunSuite:
    test("topologicalSort returns sorted list for acyclic graph"):
        val items                                = List('A', 'B', 'C', 'D', 'E')
        val dependencies: Char => Iterable[Char] =
            case 'A' => List('D')
            case 'B' => List('D')
            case 'C' => List('A', 'B')
            case 'D' => List('E')
            case 'E' => List()
        end dependencies
        assertEquals(items.topologicalSort(dependencies), Right(List('E', 'D', 'A', 'B', 'C')))

    test("topologicalSort returns error for cyclic graph"):
        val items                                = List('A', 'B', 'C')
        val dependencies: Char => Iterable[Char] =
            case 'A' => List('B')
            case 'B' => List('C')
            case 'C' => List('A')
        assertEquals(items.topologicalSort(dependencies), Left("Cyclic dependency found"))

    test("topologicalSort returns sorted list for single node graph"):
        val items                                = List('A')
        val dependencies: Char => Iterable[Char] =
            case 'A' => List()
        assertEquals(items.topologicalSort(dependencies), Right(List('A')))

    test("topologicalSort returns sorted list for disconnected graph"):
        val items                                = List('A', 'B', 'C', 'D', 'E')
        val dependencies: Char => Iterable[Char] = _ => List.empty
        assertEquals(items.topologicalSort(dependencies), Right(items))
end TopologicalSortSuite
