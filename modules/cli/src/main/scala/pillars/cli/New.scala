package pillars.cli

import com.monovore.decline.*

final case class New()

object New:
  val command: Command[New] = Command(
    name = "new",
    header = "Generate a new Pillars project",
    helpFlag = true
  ) {
    Opts.subcommand("project", "Generate a new Pillars project") {
      Opts.unit.map(_ => New())
    }
  }
end New
