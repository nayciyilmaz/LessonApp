package com.example.lessonapp.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class DailyStatistic(
    val dayName: String,
    val date: String,
    val startDate: String,
    val endDate: String,
    val lessonTimes: Map<String, String>,
    val totalTime: String
)

data class WeeklyStatistic(
    val startDate: String,
    val endDate: String,
    val lessonTimes: Map<String, String>,
    val totalTime: String
)

data class MonthlyStatistic(
    val month: String,
    val totalTime: String
)


class StatisticsCalculator {
    fun getWeekStartEndDates(date: Calendar): Pair<Calendar, Calendar> {
        val startDate = Calendar.getInstance().apply {
            timeInMillis = date.timeInMillis
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }

        val endDate = Calendar.getInstance().apply {
            timeInMillis = startDate.timeInMillis
            add(Calendar.DAY_OF_WEEK, 6)
        }

        return Pair(startDate, endDate)
    }

    fun formatTimeFromSeconds(seconds: Long): String {
        val hrs = seconds / 3600
        val mins = (seconds % 3600) / 60
        val secs = seconds % 60
        return "%02d:%02d:%02d".format(hrs, mins, secs)
    }

    fun formatDate(calendar: Calendar, locale: Locale = Locale("tr", "TR")): String {
        val formatter = SimpleDateFormat("dd MMMM", locale)
        return formatter.format(calendar.time)
    }

    fun formatMonth(calendar: Calendar, locale: Locale = Locale("tr", "TR")): String {
        val formatter = SimpleDateFormat("MMMM yyyy", locale)
        return formatter.format(calendar.time).capitalize(locale)
    }

    fun getDayName(calendar: Calendar, locale: Locale = Locale("tr", "TR")): String {
        val formatter = SimpleDateFormat("EEEE", locale)
        return formatter.format(calendar.time).capitalize(locale)
    }
}
