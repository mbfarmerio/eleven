import cats.effect.{ExitCode, IO, IOApp}
import http.V1
import org.http4s.server.blaze.BlazeServerBuilder
import cats.implicits._
import models.BuildingResponse
import io.circe.generic.auto._
import io.circe.parser._
import io.circe._
import logic.FileLoader
import org.http4s.server.middleware.CORS


object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    for {
      string <- FileLoader.loadResource("buildingResponse.json")
      json = parse(string).getOrElse(Json.Null)
      buildings = json.as[BuildingResponse].getOrElse(BuildingResponse(Seq.empty))
      server <- BlazeServerBuilder[IO]
        .bindHttp(9001, "localhost")
        .withHttpApp(CORS(V1.routes(buildings)))
        .serve
        .compile
        .drain
        .as(ExitCode.Success)
    } yield server
}
