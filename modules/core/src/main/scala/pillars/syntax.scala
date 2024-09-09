package pillars

import scala.annotation.targetName

object syntax:
    object all:
        export language.*

    object language:
        extension [A](a: A)
            @targetName("pipe")
            inline def |>[B](f: A => B): B = f(a)

end syntax
