package http

import cats.effect._
import models.BuildingResponse
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._
import io.circe.syntax._
import io.circe.generic.auto._
import cats.data.Kleisli
import logic.Utilization
import org.http4s.circe._

import scala.util.{Failure, Success, Try}
object V1 {

  def routes(buildings: BuildingResponse): Kleisli[IO, Request[IO], Response[IO]] = HttpRoutes.of[IO] {
    /**
      * Get the buildings
      */
    case GET -> Root / "buildings" =>
      for {
        json <- IO.pure(buildings.asJson)
        response <- Ok(json)
      } yield response

    case GET -> Root / "previewSpaces" / IntVar(buildingId) / IntVar(floorId)
      :? SpaceTypeQueryParameter(spaceTypeId)
      +& UsagePercentQueryParameter(usage)
      +& FrequencyQueryParameter(frequency) =>
    for {
      spaces <- IO.pure(Utilization(buildings, buildingId, floorId, spaceTypeId, usage, frequency.getOrElse("1d")))
      json <- spaces.utilization.getOrElse(Seq.empty)
      response <- Ok(json.asJson)
    } yield response

    case  POST -> Root / "createTicket" / IntVar(buildingId) / IntVar(floorId)
      :? SpaceTypeQueryParameter(spaceTypeId)
      +& UsagePercentQueryParameter(usage)
      +& FrequencyQueryParameter(frequency) =>
      Try(for {
        spaces <- IO.pure(Utilization(buildings, buildingId, floorId, spaceTypeId, usage, frequency.getOrElse("1d")))
        json <- spaces.buildTickets.getOrElseF(IO.raiseError(new Exception("Error, could not create ticket.  Either an invalid floor or building has been supplied")))
        response <- Ok(json.asJson)
      } yield response) match {
        case Success(response) => response
        case Failure(ex) => InternalServerError(ex.getMessage)
      }

  }.orNotFound
}

object SpaceTypeQueryParameter extends QueryParamDecoderMatcher[Int]("spaceType")
object UsagePercentQueryParameter extends QueryParamDecoderMatcher[Int]("usage")
object FrequencyQueryParameter extends OptionalQueryParamDecoderMatcher[String]("frequency")
