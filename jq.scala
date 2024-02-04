//> using toolkit typelevel:latest
//> using dep io.circe::circe-parser:0.14.6
//> using dep com.monovore::decline-effect:2.4.1

import cats.effect.*
import cats.implicits.*
import com.monovore.decline.*
import com.monovore.decline.effect.*
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.*
import fs2.io.*
import fs2.Stream.eval

object Main extends CommandIOApp(name = "jq", header = "jq")  {
  val filterOpts: Opts[String] = Opts.argument[String](metavar = "filter")
  val inputOpts: Opts[String] = Opts.argument[String](metavar = "input")

  def program(filter: String, input: String) = for {
    _ <- IO.println(filter)
    json <- IO.fromEither(parse(input))
    result = json \\ filter
    _ <- IO.println(result.asJson)
  } yield ExitCode.Success

  def main: Opts[IO[ExitCode]] =
    (filterOpts, inputOpts).mapN(program)
}

// scala-cli jq.scala -- 'quotes' '{"quotes":[{"id":1,"quote":"Life isn’t about getting and having, it’s about giving and being.","author":"Kevin Kruse"},{"id":2,"quote":"Whatever the mind of man can conceive and believe, it can achieve.","author":"Napoleon Hill"}],"total":100,"skip":0,"limit":2}'

