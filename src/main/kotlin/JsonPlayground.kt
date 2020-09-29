import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class Event(
  val title: String,
  val beginDateTime: String,
  val endDateTime: String
)

fun main() {
  val json = Json.encodeToString(
    Event(
      title = "Team retro",
      beginDateTime = "20.08.2020 16:00",
      endDateTime = "20.08.2020 17:00",
    )
  )
  println(json)
}
