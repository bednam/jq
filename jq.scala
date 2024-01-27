//> using toolkit typelevel:latest
//> using dep io.circe::circe-parser:0.14.6
//> using dep com.monovore::decline-effect:2.4.1

import cats.effect.*
import cats.implicits.*
import com.monovore.decline.*
import com.monovore.decline.effect.*
import io.circe.*
import io.circe.parser.*
import fs2.io.* 
import fs2.Stream.eval

object Main extends IOApp.Simple {
    case class ParseJson(filter: String)

    val filterOpts: Opts[String] = Opts.argument[String](metavar = "filter")
    def run = 
        for {
            result <- stdinUtf8[IO](1024).compile.lastOrError
            json <- IO.fromEither(parse(result))
            _ <- IO.println(json)
        } yield ()
    // def run(args: List[String]): IO[ExitCode] = {

    //     val json = args.head
    //     println(json)
    //     IO.println(parse(json))
    // }.as(ExitCode.Success)

}

