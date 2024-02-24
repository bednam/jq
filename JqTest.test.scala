//> using dep org.typelevel::munit-cats-effect:2.0.0-M4
//> using file jq.scala

import cats.effect.{IO, SyncIO}
import munit.FunSuite

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

    test("extract nested field") {
        val in = """{ "key": {"key": "value" } }"""
        val out = "\"value\""
        Main.program(".key.key", in).assertEquals(out)
    }

    test("return null on missing key") {
        val in = """{ "key": "value" }"""
        val out = null
        Main.program(".keyy", in).assertEquals(out)        
    }

    test("extract object from array") {
        val in = """[{ "key": "value1" }, { "key": "value2" }]"""
        val out = """{ "key": "value1" }"""
        Main.program(".[0]", in).assertEquals(out)
    }    

}
