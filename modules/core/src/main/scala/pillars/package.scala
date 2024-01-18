package object pillars:
    extension [T](items: Iterable[T])
        /**
         * Extension method for Iterable[T] to perform topological sorting.
         *
         * @param dependencies A function that takes an item of type T and returns an Iterable[T] of its dependencies.
         * @return Either a sorted List[T] if the graph is acyclic, or an error message if a cycle is detected.
         */
        def topologicalSort(dependencies: T => Iterable[T]): Either[String, List[T]] =
            @annotation.tailrec
            def loop(
                remaining: Iterable[T],
                sorted: List[T],
                visited: Set[T],
                recursionStack: Set[T]
            ): Either[String, List[T]] =
                if remaining.isEmpty then Right(sorted)
                else
                    val (noDeps, hasDeps) = remaining.partition(item => dependencies(item).forall(visited.contains))
                    if noDeps.isEmpty then
                        if hasDeps.exists(recursionStack.contains) then Left("Cyclic dependency found")
                        else loop(hasDeps, sorted, visited, recursionStack ++ hasDeps)
                    else loop(hasDeps, sorted ++ noDeps.toList, visited ++ noDeps, recursionStack -- noDeps)

            loop(items, List.empty, Set.empty, Set.empty)
end pillars
