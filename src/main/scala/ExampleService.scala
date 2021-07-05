import ExampleData._
import zio._

object ExampleService {
  type ExampleService = Has[Service]

  trait Service {
    def getCharacters(origin: Option[Origin]): UIO[List[Character]]
  }

  def getCharacters(
      origin: Option[Origin]
  ): URIO[ExampleService, List[Character]] =
    URIO.serviceWith(_.getCharacters(origin))

  def live(initial: List[Character]): ZLayer[Any, Nothing, ExampleService] =
    (for {
      characters <- Ref.make(initial)
      subscribers <- Hub.unbounded[String]
    } yield new Service {
      def getCharacters(origin: Option[Origin]): UIO[List[Character]] =
        characters.get.map(_.filter(c => origin.forall(c.origin == _)))
    }).toLayer
}
