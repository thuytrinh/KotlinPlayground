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
  val expected = Event(
    title = "Team retro",
    beginDateTime = "20.08.2020 16:00",
    endDateTime = "20.08.2020 17:00",
  )
  val json = Json.encodeToString(expected)
  val actual = Json.decodeFromString(Event.serializer(), json)
  println(json)
  println(actual)
}
