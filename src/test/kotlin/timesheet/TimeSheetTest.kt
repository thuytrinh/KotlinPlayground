package timesheet

import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test
import java.io.File
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

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
    .map {
      val morning = it.atTime(hour = 9, minute = 0, second = 0).toJavaLocalDateTime()
      val beforeLunch = it.atTime(hour = 12, minute = 0, second = 0).toJavaLocalDateTime()
      val afterLunch = it.atTime(hour = 12, minute = 30, second = 0).toJavaLocalDateTime()
      val end = it.atTime(hour = 17, minute = 30, second = 0).toJavaLocalDateTime()

      val totalWorkingHoursInDay = ChronoUnit.HOURS.between(
        morning,
        beforeLunch
      ) + ChronoUnit.HOURS.between(afterLunch, end)

      val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
      listOf(
        TimeEntry(
          startDate = formatter.format(morning),
          endDate = formatter.format(beforeLunch),
          location = "Bosch eBike Digital",
          type = "timer",
        ),
        TimeEntry(
          startDate = formatter.format(afterLunch),
          endDate = formatter.format(end),
          location = "Bosch eBike Digital",
          type = "timer",
        ),
      ) to totalWorkingHoursInDay
    }
    .run {
      val totalWorkingHoursInMonth = map { it.second }.sum()
      TimeSheet(flatMap { it.first }) to totalWorkingHoursInMonth
    }
    .apply {
      println("Total working hours in $yearMonth: $second")
      File("build", "thuy-trinh-$yearMonth.json")
        .writeText(Json.encodeToString(first))
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