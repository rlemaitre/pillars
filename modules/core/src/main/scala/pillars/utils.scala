package pillars

import scala.annotation.targetName

object utils:
  extension [A](a: A)
    @targetName("pipe")
    def |>[B](f: A => B): B = f(a)
    @targetName("tap")
    def <|(f: A => Unit): A =
      f(a)
      a
