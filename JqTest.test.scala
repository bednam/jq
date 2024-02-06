//> using dep org.typelevel::munit-cats-effect:2.0.0-M4
//> using file jq.scala

import cats.effect.{IO, SyncIO}
import munit.CatsEffectSuite

class JqTest extends CatsEffectSuite {
    // test("minimal") {
    //     Main.program(".", "{}").assertEquals("{}")
    // }

    // test("format json") {
    //     val in = """{ "key": "value" }"""
    //     val out = 
    //         """|{
    //            |  "key" : "value"
    //            |}""".stripMargin
    //     Main.program(".", in).assertEquals(out)
    // }    

    test("extract field") {
        val in = """{ "key": "value" }"""
        val out = "\"value\""
        Main.program(".key", in).assertEquals(out)
    }    
}
