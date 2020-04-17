package logic

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

import cats.data.OptionT
import cats.effect._
import models.{BuildingResponse, MaintenanceRequest, Space, SpaceType}

case class Utilization(buildings: BuildingResponse, buildingId: Int, floorId: Int, spaceTypeId: Int , threshold: Int, frequency: String) {

  private val hourly = "(^[0-9]{1,1}[h]{1,1}$)"
  private val daily = "(^[0-9]{1,1}[d]{1,1}$)"
  private val weekly = "(^[0-9]{1,1}[w]{1,1}$)"

  val spaceType = for {
    building <- OptionT.fromOption[IO](buildings.asMap.get(buildingId))
    floor <- OptionT.fromOption[IO](building.asMap.get(floorId))
    spaceType <- OptionT.fromOption[IO](floor.asMap.get(spaceTypeId))
  } yield spaceType

  val adjustedDate = frequency match {
    case h if h.matches(hourly) =>
      val num = h.take(1).toLong
      LocalDateTime.now().minus(num, ChronoUnit.HOURS)
    case d if d.matches(daily) =>
      val num = d.take(1).toLong
      LocalDateTime.now().minus(num, ChronoUnit.DAYS)
    case w if w.matches(weekly) =>
      val num = w.take(1).toLong
      LocalDateTime.now().minus(num, ChronoUnit.WEEKS)
    case _ => LocalDateTime.now().minus(1, ChronoUnit.DAYS)
  }

  def adjustedTimeUtilization(space: SpaceType): Seq[Space] = frequency match {
    case h if h.matches(hourly) =>
      val num = h.take(1).toInt
      space.withinHour(threshold, 60 * num)
    case d if d.matches(daily) =>
      val num = d.take(1).toInt
      space.withinDay(threshold, 540 * num)
    case w if w.matches(weekly) =>
      val num = w.take(1).toInt
      space.withinWeek(threshold, 2700 * num)
    case _ => space.withinDay(threshold, 540)
  }

  def utilization: OptionT[IO, Seq[Space]] = for {
    s <- spaceType
    utilization = adjustedTimeUtilization(s)
  } yield utilization

  def buildTickets: OptionT[IO, MaintenanceRequest] = for {
    s <- utilization
    building <- OptionT.fromOption[IO](buildings.asMap.get(buildingId))
    floor <- OptionT.fromOption[IO](building.asMap.get(floorId))
  } yield MaintenanceRequest(building.name, floor.name, s)
}
