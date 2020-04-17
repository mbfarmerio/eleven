package models
import java.time.temporal.WeekFields
import java.time.{Instant, LocalDateTime, ZoneId}
import java.util.Locale

case class BuildingResponse(buildings: Seq[Building]) {

  def asMap: Map[Int, Building] = buildings.map{building => building.id -> building}.toMap
}

case class Building(id: Int, name: String, floors: Seq[Floor]) {

  def asMap: Map[Int, Floor] = floors.map{floor => floor.id -> floor}.toMap
}

case class Floor(id: Int, name: String, spaceTypes: Seq[SpaceType]) {

  def asMap: Map[Int, SpaceType] = spaceTypes.map{types => types.id -> types}.toMap
}

case class SpaceType(id: Int, name: String, spaces: Seq[Space]) {

  def asMap: Map[Int, Space] = spaces.map{space => space.id -> space}.toMap

  def withinHour(threshold: Int, minutes: Long): Seq[Space] = spaces.filter{
    space =>
      val percentage = (space.utilization.hour.asInstanceOf[Double] / minutes) * 100
      val lastSanitized = Instant.ofEpochMilli(space.lastSanitized).atZone(ZoneId.systemDefault()).toLocalDateTime.getHour
      val currentTime = LocalDateTime.now().getHour
      percentage >= threshold.asInstanceOf[Double] && lastSanitized != currentTime
  }

  def withinDay(threshold: Int, minutes: Long): Seq[Space] = spaces.filter{
    space =>
      val percentage = (space.utilization.day.asInstanceOf[Double] / minutes) * 100
      val lastSanitized = Instant.ofEpochMilli(space.lastSanitized).atZone(ZoneId.systemDefault()).toLocalDateTime.getDayOfMonth
      val currentTime = LocalDateTime.now().getDayOfMonth
      percentage >= threshold.asInstanceOf[Double] && lastSanitized != currentTime
  }

  def withinWeek(threshold: Int, minutes: Long): Seq[Space] = spaces.filter{
    space =>
      val woy = WeekFields.of(Locale.getDefault).weekOfWeekBasedYear
      val percentage = (space.utilization.week.asInstanceOf[Double] / minutes) * 100
      val lastSanitized = Instant.ofEpochMilli(space.lastSanitized).atZone(ZoneId.systemDefault()).toLocalDateTime.get(woy)
      val currentTime = LocalDateTime.now().get(woy)
      percentage >= threshold.asInstanceOf[Double] && lastSanitized != currentTime
  }

  def withinFrequency(frequency: LocalDateTime): Seq[Space] = spaces.filter{
    space =>
    space.sanitized.compareTo(frequency) >= 1
  }
}

case class Space(id: Int, name: String, utilization: Usage, lastSanitized: Long) {

  def sanitized: LocalDateTime = Instant.ofEpochMilli(lastSanitized).atZone(ZoneId.systemDefault()).toLocalDateTime
}

case class Usage(hour: Int, day: Int, week: Int)
