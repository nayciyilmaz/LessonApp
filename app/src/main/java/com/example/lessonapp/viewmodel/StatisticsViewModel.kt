package com.example.lessonapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lessonapp.model.DailyStatistic
import com.example.lessonapp.model.MonthlyStatistic
import com.example.lessonapp.model.StatisticsCalculator
import com.example.lessonapp.model.WeeklyStatistic
import com.example.lessonapp.repository.LessonRepository
import com.example.lessonapp.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _dailyStatistics = MutableStateFlow<List<DailyStatistic>>(emptyList())
    val dailyStatistics: StateFlow<List<DailyStatistic>> = _dailyStatistics.asStateFlow()

    private val _weeklyStatistics = MutableStateFlow<List<WeeklyStatistic>>(emptyList())
    val weeklyStatistics: StateFlow<List<WeeklyStatistic>> = _weeklyStatistics.asStateFlow()

    private val _monthlyStatistics = MutableStateFlow<List<MonthlyStatistic>>(emptyList())
    val monthlyStatistics: StateFlow<List<MonthlyStatistic>> = _monthlyStatistics.asStateFlow()

    private val statisticsCalculator = StatisticsCalculator()

    init {
        loadStatistics()
    }

    fun loadStatistics() {
        viewModelScope.launch {
            loadDailyStatistics()
            loadWeeklyStatistics()
            loadMonthlyStatistics()
        }
    }

    private suspend fun loadDailyStatistics() {
        val currentDate = Calendar.getInstance()
        val (weekStart, weekEnd) = statisticsCalculator.getWeekStartEndDates(currentDate)
        val startDateStr = statisticsCalculator.formatDate(weekStart)
        val endDateStr = statisticsCalculator.formatDate(weekEnd)
        val dailyStats = mutableListOf<DailyStatistic>()

        val tempDate = Calendar.getInstance().apply { timeInMillis = weekStart.timeInMillis }
        for (i in 0 until 7) {
            val dayStart = Calendar.getInstance().apply {
                timeInMillis = tempDate.timeInMillis
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val dayEnd = Calendar.getInstance().apply {
                timeInMillis = tempDate.timeInMillis
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }

            val dayNotes =
                noteRepository.getNotesByDateRange(dayStart.timeInMillis, dayEnd.timeInMillis)
            val lessonTimes = mutableMapOf<String, Long>()
            var totalTimeSeconds = 0L

            dayNotes.forEach { note ->
                val lessonId = note.lessonId
                val lesson = lessonRepository.getLessonById(lessonId)
                val lessonName = lesson?.lessonName ?: "UNKNOWN"

                val timeInSeconds = note.studyTimeInMillis
                lessonTimes[lessonName] = (lessonTimes[lessonName] ?: 0L) + timeInSeconds
                totalTimeSeconds += timeInSeconds
            }

            val formattedLessonTimes = lessonTimes.mapValues {
                statisticsCalculator.formatTimeFromSeconds(it.value)
            }

            val dailyStat = DailyStatistic(
                dayName = statisticsCalculator.getDayName(tempDate),
                date = statisticsCalculator.formatDate(tempDate),
                startDate = startDateStr,
                endDate = endDateStr,
                lessonTimes = formattedLessonTimes,
                totalTime = statisticsCalculator.formatTimeFromSeconds(totalTimeSeconds)
            )

            dailyStats.add(dailyStat)
            tempDate.add(Calendar.DAY_OF_MONTH, 1)
        }
        _dailyStatistics.value = dailyStats
    }

    private suspend fun loadWeeklyStatistics() {
        val currentDate = Calendar.getInstance()
        val weeklyStats = mutableListOf<WeeklyStatistic>()

        for (weekOffset in 0 downTo -3) {
            val weekDate = Calendar.getInstance().apply {
                timeInMillis = currentDate.timeInMillis
                add(Calendar.WEEK_OF_YEAR, weekOffset)
            }

            val (weekStart, weekEnd) = statisticsCalculator.getWeekStartEndDates(weekDate)
            val weekNotes = noteRepository.getNotesByDateRange(
                weekStart.timeInMillis,
                weekEnd.timeInMillis
            )

            val lessonTimes = mutableMapOf<String, Long>()
            var totalTimeSeconds = 0L

            weekNotes.forEach { note ->
                val lessonId = note.lessonId
                val lesson = lessonRepository.getLessonById(lessonId)
                val lessonName = lesson?.lessonName ?: "UNKNOWN"

                val timeInSeconds = note.studyTimeInMillis
                lessonTimes[lessonName] = (lessonTimes[lessonName] ?: 0L) + timeInSeconds
                totalTimeSeconds += timeInSeconds
            }

            val formattedLessonTimes = lessonTimes.mapValues {
                statisticsCalculator.formatTimeFromSeconds(it.value)
            }

            if (weekNotes.isNotEmpty()) {
                val weeklyStat = WeeklyStatistic(
                    startDate = statisticsCalculator.formatDate(weekStart),
                    endDate = statisticsCalculator.formatDate(weekEnd),
                    lessonTimes = formattedLessonTimes,
                    totalTime = statisticsCalculator.formatTimeFromSeconds(totalTimeSeconds)
                )
                weeklyStats.add(weeklyStat)
            }
        }
        _weeklyStatistics.value = weeklyStats
    }

    private suspend fun loadMonthlyStatistics() {
        val allNotes = noteRepository.getAllNotes()
        val notesByMonth = allNotes.groupBy { note ->
            val calendar = Calendar.getInstance().apply {
                timeInMillis = note.date
            }
            "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)}"
        }

        val monthlyStats = notesByMonth.map { (yearMonth, notes) ->
            val (year, month) = yearMonth.split("-").map { it.toInt() }

            val calendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, 1)
            }

            val totalTimeSeconds = notes.sumOf { it.studyTimeInMillis }

            MonthlyStatistic(
                month = statisticsCalculator.formatMonth(calendar),
                totalTime = statisticsCalculator.formatTimeFromSeconds(totalTimeSeconds)
            )
        }.sortedByDescending { it.month }

        _monthlyStatistics.value = monthlyStats
    }

    fun formatTime(seconds: Long): String {
        return statisticsCalculator.formatTimeFromSeconds(seconds)
    }
}