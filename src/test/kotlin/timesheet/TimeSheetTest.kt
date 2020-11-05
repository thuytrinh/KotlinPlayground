package timesheet

import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test
import java.io.File
import java.time.DayOfWeek
import java.time.YearMonth

class TimeSheetTest {
  @Test
  fun `time-sheet for September`() {
    generateFor(YearMonth.of(2020, Month.SEPTEMBER))
  }
}

private fun generateFor(yearMonth: YearMonth) {
  val firstDate = LocalDate(year = yearMonth.year, month = yearMonth.month, 1)
  val dayCount = firstDate.daysUntil(firstDate.plus(1, DateTimeUnit.MONTH))
  (0 until dayCount)
    .map { firstDate.plus(it, DateTimeUnit.DAY) }
    .filterNot { it.dayOfWeek == DayOfWeek.SATURDAY || it.dayOfWeek == DayOfWeek.SUNDAY }
    .flatMap {
      val morning = it.atTime(hour = 9, minute = 0)
      val beforeLunch = it.atTime(hour = 12, minute = 0)
      val afterLunch = it.atTime(hour = 12, minute = 30)
      val end = it.atTime(hour = 17, minute = 30)

      listOf(
        TimeEntry(
          startDate = morning.toString(),
          endDate = beforeLunch.toString(),
          location = "Bosch eBike Digital",
          type = "timer",
        ),
        TimeEntry(
          startDate = afterLunch.toString(),
          endDate = end.toString(),
          location = "Bosch eBike Digital",
          type = "timer",
        ),
      )
    }
    .run { TimeSheet(this) }
    .apply {
      File("build", "thuy-trinh-$yearMonth.json")
        .writeText(Json.encodeToString(this))
    }
}

@Serializable
private data class TimeSheet(
  val timers: List<TimeEntry>,
)

@Serializable
private data class TimeEntry(
  val location: String,
  val type: String,
  val startDate: String,
  val endDate: String,
)
