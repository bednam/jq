//> using toolkit typelevel:0.1.23
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
    case ObjectDown(key, next, optional) if optional => {
      val cursor = json.downField(key)
      cursor.focus.fold("null")(_ => extract(next, cursor))
    }
    case ObjectDown(key, next, _) => extract(next, json.downField(key))
    case ArrayDown(index, next) => extract(next, json.downN(index))
    case RootDown => json.focus.map(_.toString).getOrElse(null)
  }  

  def main: Opts[IO[ExitCode]] =
    (filterOpts, inputOpts).mapN(program).map(_.flatTap(IO.println)).map(_.as(ExitCode.Success))
}
