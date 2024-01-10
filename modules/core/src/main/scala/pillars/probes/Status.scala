package pillars.probes

import cats.kernel.Monoid

enum Status:
  case pass, warn, fail

object Status:
  given Monoid[Status] with
    def empty: Status = Status.pass
    def combine(x: Status, y: Status): Status =
      (x, y) match
        case (Status.pass, Status.pass) => Status.pass
        case (Status.fail, _)           => Status.fail
        case (_, Status.fail)           => Status.fail
        case _                          => Status.warn
