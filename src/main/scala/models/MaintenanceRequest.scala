package models
import java.time.{LocalDate, LocalDateTime}

import scala.util.Random

case class MaintenanceRequest(
                               building: String,
                               floor: String,
                               spaces: Seq[Space],
                               dateEntered: LocalDateTime = LocalDateTime.now,
                               dateRequired: LocalDateTime = LocalDateTime.now.plusMinutes(20L),
                               requestType: String = "Sanitation",
                               operators: Seq[MaintenanceOperators] = MaintenanceRequest.getOperators,
                               instructions: Seq[String] = MaintenanceRequest.getInstructions,
                               title: String = "Sanitation",
                               orderNumber: Int = 1000 + Random.nextInt(( 5000 - 1000) + 1),
                             )

object MaintenanceRequest {

  def getOperators =
    Seq(
      MaintenanceOperators("Clean", "Veritably", "https://www.logosurfer.com/wp-content/uploads/2018/03/mr-clean-logo_0.jpg"),
      MaintenanceOperators("Man", "Brawny", "https://images-na.ssl-images-amazon.com/images/I/917PL9AVdPL._SL1500_.jpg"),
      MaintenanceOperators("Wolf", "The", "https://media3.giphy.com/media/GnEyBQ2M2Vub6/giphy.gif?cid=ecf05e47353fae3cce86607d79b5586ac81659f25f7a8752&rid=giphy.gif"),
      MaintenanceOperators("Sparkle", "Mr", "https://pbs.twimg.com/media/BwCp5tuIUAAFM8W.jpg")
    )
  def getInstructions =
    Seq(
      "Sterilize hard surfaces",
      "Replenish hand sanitizer if needed",
      "Replenish tissues if needed"
    )
}
case class MaintenanceOperators(lastName: String, firstName: String, image: String)
