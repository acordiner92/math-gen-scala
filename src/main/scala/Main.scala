import zio._
import ExampleData._
import ExampleService._
import GraphqlServer._
import caliban.ZHttpAdapter
import zhttp.service.Server
import zhttp.http._
import zio.stream.ZStream

object ExampleApp extends App {
  private val graphiql = Http.succeed(
    Response.http(content =
      HttpData.fromStream(ZStream.fromResource("graphiql.html"))
    )
  )

  override def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] =
    (for {
      interpreter <- GraphqlServer.api.interpreter
      _ <- Server
        .start(
          8088,
          Http.route {
            case _ -> Root / "api" / "graphql" =>
              ZHttpAdapter.makeHttpService(interpreter)
            case _ -> Root / "ws" / "graphql" =>
              ZHttpAdapter.makeWebSocketService(interpreter)
            case _ -> Root / "graphiql" => graphiql
          }
        )
        .forever
    } yield ())
      .provideCustomLayer(ExampleService.live(sampleCharacters))
      .exitCode
}
