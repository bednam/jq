//> using dep org.typelevel::munit-cats-effect:2.0.0-M4
//> using files jq.scala, Down.scala

import cats.effect.{IO, SyncIO}
import munit.CatsEffectSuite

class JqTest extends CatsEffectSuite {
    test("minimal") {
        Main.program(".", "{}").assertEquals("{}")
    }

    test("format json") {
        val in = """{ "key": "value" }"""
        val out = 
            """|{
               |  "key" : "value"
               |}""".stripMargin
        Main.program(".", in).assertEquals(out)
    }    

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

    test("extract field with optional syntax") {
        val in = """{ "key": "value" }"""
        val out = "\"value\""        
        Main.program(".key?", in).assertEquals(out)
    }

    test("return null with optional syntax when key does not exist") {
        val in = """{ "key": "value" }"""
        val out = "null"                
        Main.program(".not_existing_key?", in).assertEquals(out)
    }       

    test("extract value from array") {
        val in = """["1", "2"]"""
        val out = "\"1\""
        Main.program(".[0]", in).assertEquals(out)
    }

    test("extract object from array") {
        val in = """[{"key": "value1"}, {"key": "value2"}]"""
        val out =  """|{
                      |  "key" : "value1"
                      |}""".stripMargin 
        Main.program(".[0]", in).assertEquals(out)
    }    

    test("extract field with array syntax") {
        val in = """{ "key": "value" }"""
        val out = "\"value\""        
        Main.program(".[\"key\"]", in).assertEquals(out)
    }

    test("extract field with array optional syntax") {
        val in = """{ "key": "value" }"""
        val out = "\"value\""        
        Main.program(""".["key"]?""", in).assertEquals(out)
    }

    test("return null with array optional syntax when key does not exist") {
        val in = """{ "key": "value" }"""
        val out = "null"        
        Main.program(""".["not_existing_key"]?""", in).assertEquals(out)
    }     
}
