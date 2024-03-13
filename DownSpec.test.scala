//> using dep org.typelevel::munit-cats-effect:2.0.0-M4
//> using file Down.scala

import cats.effect.{IO, SyncIO}
import munit.FunSuite

class JqTest extends FunSuite {
    test("parse root") {
        val in = """."""
        val expected = RootDown
        val actual = Down.parseDown(in)

        assertEquals(expected, actual)
    }

    test("parse root index") {
        val in = """.[0]"""
        val expected = ArrayDown(0, RootDown)
        val actual = Down.parseDown(in)

        assertEquals(actual, expected)
    }

    test("parse nested index") {
        val in = """.[0].[0]"""
        val expected = ArrayDown(0, ArrayDown(0, RootDown))
        val actual = Down.parseDown(in)

        assertEquals(actual, expected)
    }

    test("parse key") {
        val in = """.key"""
        val expected = ObjectDown("key", RootDown)
        val actual = Down.parseDown(in)

        assertEquals(actual, expected)        
    }

    test("parse optional key") {
        val in = """.key?"""
        val expected = ObjectDown("key", RootDown, optional = true)
        val actual = Down.parseDown(in)

        assertEquals(actual, expected)                
    }

    test("parse nested key") {
        val in = """.key.key"""
        val expected = ObjectDown("key", ObjectDown("key", RootDown))
        val actual = Down.parseDown(in)

        assertEquals(actual, expected)           
    }

    test("parse field in array") {
        val in = """.["key"]"""
        val expected = ObjectDown("key", RootDown)
        val actual = Down.parseDown(in)

        assertEquals(actual, expected)
    }

    test("parse optional field in array") {
        val in = """.["key"]?"""
        val expected = ObjectDown("key", RootDown, optional = true)
        val actual = Down.parseDown(in)

        assertEquals(actual, expected)
    }    

    test("parse with pipe operator") {
        val in = """.[0] | .key"""
        val expected = ArrayDown(0, ObjectDown("key", RootDown, optional = false))
        val actual = Down.parseDown(in)

        assertEquals(actual, expected)
    }

    test("parse key with brackets") {
        val in = """.key[]"""
        val expected = ObjectDown("key", RootDown, brackets = true)
        val actual = Down.parseDown(in)

        assertEquals(actual, expected)
    }

    test("parse optional key with brackets") {
        val in = """.key[]?"""
        val expected = ObjectDown("key", RootDown, optional = true, brackets = true)
        val actual = Down.parseDown(in)

        assertEquals(actual, expected)
    }    
}