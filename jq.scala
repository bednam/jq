//> using toolkit typelevel:0.1.21
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
import fs2.io.*
import fs2.Stream.eval
import scala.collection.View.Filter

object Main extends CommandIOApp(name = "jq", header = "jq")  {
  val filterOpts: Opts[String] = Opts.argument[String](metavar = "filter")
  val inputOpts: Opts[String] = Opts.argument[String](metavar = "input")

  def program(filter: String, input: String): IO[String] = 
  for {
    json <- IO.fromEither(parse(input))
    extract = filter.split("\\.").toList.drop(1)
    acursor = extract.foldLeft[ACursor](json.hcursor)((acc, curr) => acc.downField(curr))
  } yield acursor.as[String].toOption.map('"' + _ + '"').getOrElse(null)

  def main: Opts[IO[ExitCode]] =
    (filterOpts, inputOpts).mapN(program).map(_.as(ExitCode.Success))
}

// scala-cli jq.scala -- 'quotes' '{"quotes":[{"id":1,"quote":"Life isn’t about getting and having, it’s about giving and being.","author":"Kevin Kruse"},{"id":2,"quote":"Whatever the mind of man can conceive and believe, it can achieve.","author":"Napoleon Hill"}],"total":100,"skip":0,"limit":2}'
// ccjq '.codingchallenge'
// ccjq '.["codingchallenge"]'
// ccjq '.codingchallenge?'
// ccjq '.["codingchallenge"]?'

