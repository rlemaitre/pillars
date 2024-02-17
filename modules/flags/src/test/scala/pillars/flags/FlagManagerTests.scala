package pillars.flags

import cats.effect.IO
import cats.syntax.all.*
import munit.CatsEffectSuite
import org.typelevel.otel4s.trace.Tracer
import pillars.flags.*

class FlagManagerTests extends CatsEffectSuite:

    val flag1               = FeatureFlag(flag"flag1", Status.Enabled)
    val flag2               = FeatureFlag(flag"flag2", Status.Disabled)
    val config: FlagsConfig = FlagsConfig(flags = List(flag1, flag2))

    test("FlagManager should return the correct flag"):
        given Tracer[IO] = Tracer.noop[IO]
        val flag         =
            for
                manager <- FlagManagerLoader().createManager[IO](config)
                flag    <- manager.getFlag(flag"flag1")
            yield flag
        assertIO(flag, flag1.some)

    test("FlagManager should return None if flag is not found"):
        given Tracer[IO] = Tracer.noop[IO]
        val flag         =
            for
                manager <- FlagManagerLoader().createManager[IO](config)
                flag    <- manager.getFlag(flag"undefined")
            yield flag
        assertIO(flag, none)

    test("FlagManager should return the correct flag status"):
        given Tracer[IO]          = Tracer.noop[IO]
        def isEnabled(flag: Flag) =
            for
                manager <- FlagManagerLoader().createManager[IO](config)
                enabled <- manager.isEnabled(flag)
            yield enabled
        assertIO(isEnabled(flag"flag1"), true)
        assertIO(isEnabled(flag"flag2"), false)
        assertIO(isEnabled(flag"undefined"), false)

    test("FlagManager should perform the action if flag is enabled"):
        given Tracer[IO] = Tracer.noop[IO]
        var called       = false
        val performed    =
            for
                manager <- FlagManagerLoader().createManager[IO](config)
                _       <- manager.when(flag"flag1")(IO { called = true })
            yield called
        assertIO(performed, true)

    test("FlagManager should not perform the action if flag is disabled"):
        given Tracer[IO] = Tracer.noop[IO]
        var called       = false
        val performed    =
            for
                manager <- FlagManagerLoader().createManager[IO](config)
                _       <- manager.when(flag"flag2")(IO { called = true })
            yield called
        assertIO(performed, false)

    test("FlagManager should not perform the action if flag is not found"):
        given Tracer[IO] = Tracer.noop[IO]
        var called       = false
        val performed    =
            for
                manager <- FlagManagerLoader().createManager[IO](config)
                _       <- manager.when(flag"undefined")(IO { called = true })
            yield called
        assertIO(performed, false)

    test("FlagManager should correctly modify an existing flag"):
        given Tracer[IO] = Tracer.noop[IO]
        val modified     =
            for
                manager <- FlagManagerLoader().createManager[IO](config)
                _       <- manager.setStatus(flag"flag1", Status.Disabled)
                flag    <- manager.getFlag(flag"flag1")
            yield flag
        assertIO(modified, flag1.copy(status = Status.Disabled).some)

    test("FlagManager should correctly return None if flag is not found"):
        given Tracer[IO] = Tracer.noop[IO]
        val modified     =
            for
                manager <- FlagManagerLoader().createManager[IO](config)
                _       <- manager.setStatus(flag"undefined", Status.Disabled)
                flag    <- manager.getFlag(flag"undefined")
            yield flag
        assertIO(modified, none)

end FlagManagerTests
