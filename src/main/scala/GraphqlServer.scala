import caliban.schema.GenericSchema
import caliban.schema.GenericSchema
import ExampleService.ExampleService
import ExampleData._
import zio.URIO
import zio.clock.Clock
import zio.console.Console
import zio.duration._
import zio.stream.ZStream
import caliban.GraphQL
import caliban.GraphQL.graphQL
import caliban.RootResolver
import caliban.schema.Annotations.{GQLDeprecated, GQLDescription}
import caliban.schema.GenericSchema
import caliban.wrappers.ApolloTracing.apolloTracing
import caliban.wrappers.Wrappers._

object GraphqlServer extends GenericSchema[ExampleService] {
  case class Queries(
      characters: CharactersArgs => URIO[ExampleService, List[Character]]
  )

  val api: GraphQL[Console with Clock with ExampleService] =
    graphQL(
      RootResolver(
        Queries(args => ExampleService.getCharacters(args.origin))
      )
    )
}
