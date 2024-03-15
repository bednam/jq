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
      extractV2(Down.parseDown(down), json.hcursor)
    }

  def extract(down: ADown, json: ACursor, brackets: Boolean = false): String = down match {
    case ObjectDown(key, next, _, brackets) if key.isEmpty => extract(next, json, brackets)
    case ObjectDown(key, next, optional, brackets) if optional => {
      val cursor = json.downField(key)
      cursor.focus.fold("null")(_ => extract(next, cursor, brackets))
    }
    case ObjectDown(key, next, _, brackets) => extract(next, json.downField(key), brackets)
    case ArrayDown(index, next) => extract(next, json.downN(index))
    case RootDown if brackets => json.values.getOrElse(null).map(_.toString).mkString("\n")
    case RootDown => json.focus.map(_.toString).getOrElse(null)
  }  

  def extractV2(down: ADown, json: ACursor): String = {
    def toList(down: ADown): List[ADown] = down match {
      case down @ ObjectDown(_, next, _, _) => down :: toList(next)
      case down @ ArrayDown(_, next) => down :: toList(next)
      case down @ RootDown => Nil
    }

    toList(down).foldLeft(List(json))((acc, curr) => curr match {
      case ObjectDown(key, _, _, brackets) if brackets => acc.flatMap(_.downField(key).values.getOrElse(null).map(_.hcursor))
      case ObjectDown(key, _, _, _) => acc.map(_.downField(key))
      case ArrayDown(index, _) => acc.map(_.downN(index))
      case RootDown => acc
    }).map(_.focus.map(_.toString).getOrElse(null)).mkString("\n")
  }

  def main: Opts[IO[ExitCode]] =
    (filterOpts, inputOpts).mapN(program).map(_.flatTap(IO.println)).map(_.as(ExitCode.Success))
}
