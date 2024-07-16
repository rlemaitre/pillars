package pillars

import scala.annotation.targetName

object syntax:
    extension [A](a: A)
        @targetName("pipe")
        inline def |>[B](f: A => B): B = f(a)
end syntax