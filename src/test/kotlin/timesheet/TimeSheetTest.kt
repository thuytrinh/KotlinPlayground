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
  fun `generate time-sheets`() {
    generateFor(YearMonth.of(2020, Month.SEPTEMBER))
    generateFor(YearMonth.of(2020, Month.OCTOBER))
    generateFor(YearMonth.of(2020, Month.NOVEMBER))
    generateFor(YearMonth.of(2020, Month.DECEMBER), holidays = (24..31).toList())
    generateFor(YearMonth.of(2021, Month.JANUARY), holidays = listOf(1))
    generateFor(YearMonth.of(2021, Month.FEBRUARY))
    generateFor(YearMonth.of(2021, Month.MARCH))
    generateFor(YearMonth.of(2021, Month.APRIL), holidays = listOf(2, 5))
    generateFor(YearMonth.of(2021, Month.MAY), holidays = listOf(13, 14, 24))
    generateFor(YearMonth.of(2021, Month.JUNE), holidays = listOf(3, 4))
    generateFor(YearMonth.of(2021, Month.JULY), holidays = (10..23).toList())
    generateFor(YearMonth.of(2021, Month.AUGUST), holidays = emptyList())
    generateFor(YearMonth.of(2021, Month.SEPTEMBER), holidays = (15..30).toList())
  }
}

private fun generateFor(yearMonth: YearMonth, holidays: List<Int> = emptyList()) {
  val firstDate = LocalDate(year = yearMonth.year, month = yearMonth.month, dayOfMonth = 1)
  val dayCount = firstDate.daysUntil(firstDate.plus(1, DateTimeUnit.MONTH))
  (0 until dayCount)
    .map { firstDate.plus(it, DateTimeUnit.DAY) }
    .filterNot { it.dayOfWeek == DayOfWeek.SATURDAY || it.dayOfWeek == DayOfWeek.SUNDAY }
    .filterNot { it.dayOfMonth in holidays }
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
          startDate = formatter.format(morning) + "Z",
          endDate = formatter.format(beforeLunch) + "Z",
          location = "Bosch eBike Digital",
          type = "timer",
        ),
        TimeEntry(
          startDate = formatter.format(afterLunch) + "Z",
          endDate = formatter.format(end) + "Z",
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
