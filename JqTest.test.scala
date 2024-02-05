//> using dep org.typelevel::munit-cats-effect:2.0.0-M4
//> using file jq.scala

import cats.effect.{IO, SyncIO}
import munit.CatsEffectSuite

class JqTest extends CatsEffectSuite {
    test("empty string") {
         Main.program(".", "").assertEquals("")
    }

    test("minimal json") {
        Main.program(".", "{}").assertEquals("{}")
    }
}
