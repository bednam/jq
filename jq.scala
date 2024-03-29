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
import fs2.*
import fs2.io.*
import scala.util.control.NoStackTrace

object Main extends CommandIOApp(name = "jq", header = "jq") {
  val filterOpts: Opts[String] = Opts.argument[String](metavar = "filter")
  object JqError extends NoStackTrace {
    override def getMessage: String =
      "couldn't parse json with provided filter"
  }

  def program(down: String): Stream[IO, String] =
    stdinUtf8[IO](1024).evalMap { input =>
      IO.fromEither(parse(input)).flatMap { json =>
        val result = extract(Down.parseDown(down), json.hcursor :: Nil)            

        IO.raiseWhen(result.isEmpty)(JqError).as(result.mkString("", "\n", "\n"))
      }
    }

  def extract(down: ADown, cursors: List[ACursor]): List[String] = down match {
    case KeyObject(key, next, _) if key.isEmpty => extract(next, cursors)
    case KeyArray(key, next) if key.isEmpty =>
      extract(
        next,
        cursors
          .flatTraverse(_.values.map(_.map(_.hcursor).toList))
          .toList
          .flatten
      )
    case KeyArray(key, next) => {
      val nextCursorsOption: Option[List[HCursor]] = cursors.flatTraverse(
        _.downField(key).values.map(_.map(_.hcursor).toList)
      )
      nextCursorsOption.fold(List("null"))(extract(next, _))
    }
    case KeyObject(key, next, optional) if optional => {
      val nextCursors = cursors.map(_.downField(key))
      Option
        .when(nextCursors.forall(_.focus.nonEmpty))(nextCursors)
        .fold(List("null"))(extract(next, _))
    }
    case KeyObject(key, next, optional) =>
      extract(next, cursors.map(_.downField(key)))
    case IndexArray(index, next) => extract(next, cursors.map(_.downN(index)))
    case RootDown => cursors.traverse(_.focus.map(_.toString)).toList.flatten
  }

  def main: Opts[IO[ExitCode]] =
    filterOpts
      .map(
        program(_)
          .through(stdoutLines[IO, String]())
          .compile
          .drain
      )
      .map(_.as(ExitCode.Success))
}
