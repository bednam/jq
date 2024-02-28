//> using toolkit typelevel:0.1.22
//> using dep io.circe::circe-parser:0.14.6
//> using dep com.monovore::decline-effect:2.4.1
//> using file Down.scala

import cats.effect.*
import cats.implicits.*
import com.monovore.decline.*
import com.monovore.decline.effect.*
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.*

object Main extends CommandIOApp(name = "jq", header = "jq")  {
  val filterOpts: Opts[String] = Opts.argument[String](metavar = "filter")
  val inputOpts: Opts[String] = Opts.argument[String](metavar = "input")

  def program(down: String, input: String): IO[String] = 
    IO.fromEither(parse(input)).map { json => 
      extract(Down.parseDown(down), json.hcursor)
    }

  def extract(down: ADown, json: ACursor): String = down match {
    case ObjectDown(key, next) => extract(next, json.downField(key))
    case ArrayDown(index, next) => extract(next, json.downN(index))
    case RootDown => json.focus.map(_.toString).getOrElse(null)
  }  

  def main: Opts[IO[ExitCode]] =
    (filterOpts, inputOpts).mapN(program).map(_.flatTap(IO.println)).map(_.as(ExitCode.Success))
}

// scala-cli jq.scala -- '.[0]' '[{"key": "value1"}, {"key": "value2"}]'
// scala-cli jq.scala -- 'quotes' '{"quotes":[{"id":1,"quote":"Life isn’t about getting and having, it’s about giving and being.","author":"Kevin Kruse"},{"id":2,"quote":"Whatever the mind of man can conceive and believe, it can achieve.","author":"Napoleon Hill"}],"total":100,"skip":0,"limit":2}'
// ccjq '.codingchallenge'
// ccjq '.["codingchallenge"]'
// ccjq '.codingchallenge?'
// ccjq '.["codingchallenge"]?'

