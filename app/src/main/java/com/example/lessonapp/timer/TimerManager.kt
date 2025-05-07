package com.example.lessonapp.timer

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TimerManager(private val context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _timeInSeconds = MutableStateFlow(0L)
    val timeInSeconds: StateFlow<Long> = _timeInSeconds

    private var isRunning = false

    companion object {
        private const val PREFS_NAME = "timer_prefs"
        private const val KEY_START_TIME = "start_time"
        private const val KEY_PAUSED_TIME = "paused_time"
        private const val KEY_IS_RUNNING = "is_running"
        private const val KEY_ACCUMULATED_TIME = "accumulated_time"
    }

    init {
        loadTimerState()
    }

    private fun loadTimerState() {
        isRunning = prefs.getBoolean(KEY_IS_RUNNING, false)

        if (isRunning) {
            val startTimeMillis = prefs.getLong(KEY_START_TIME, 0L)
            val currentTimeMillis = System.currentTimeMillis()
            val accumulatedTime = prefs.getLong(KEY_ACCUMULATED_TIME, 0L)

            if (startTimeMillis > 0) {
                val elapsedSeconds = (currentTimeMillis - startTimeMillis) / 1000
                _timeInSeconds.value = accumulatedTime + elapsedSeconds
            }
        } else {
            val pausedTime = prefs.getLong(KEY_PAUSED_TIME, 0L)
            _timeInSeconds.value = pausedTime
        }
    }

    fun startTimer(initialTimeSeconds: Long = _timeInSeconds.value) {
        val currentTimeMillis = System.currentTimeMillis()
        val accumulatedTime = if (initialTimeSeconds > 0) initialTimeSeconds else 0L

        prefs.edit()
            .putLong(KEY_START_TIME, currentTimeMillis)
            .putLong(KEY_ACCUMULATED_TIME, accumulatedTime)
            .putBoolean(KEY_IS_RUNNING, true)
            .apply()

        isRunning = true
        _timeInSeconds.value = accumulatedTime
    }

    fun pauseTimer() {
        if (!isRunning) return

        val startTimeMillis = prefs.getLong(KEY_START_TIME, 0L)
        val currentTimeMillis = System.currentTimeMillis()
        val accumulatedTime = prefs.getLong(KEY_ACCUMULATED_TIME, 0L)

        val totalPausedTime = accumulatedTime + ((currentTimeMillis - startTimeMillis) / 1000)

        prefs.edit()
            .putLong(KEY_PAUSED_TIME, totalPausedTime)
            .putBoolean(KEY_IS_RUNNING, false)
            .apply()

        isRunning = false
        _timeInSeconds.value = totalPausedTime
    }

    fun stopTimer() {
        prefs.edit()
            .putLong(KEY_START_TIME, 0L)
            .putLong(KEY_PAUSED_TIME, 0L)
            .putLong(KEY_ACCUMULATED_TIME, 0L)
            .putBoolean(KEY_IS_RUNNING, false)
            .apply()

        isRunning = false
        _timeInSeconds.value = 0L
    }

    fun updateTimeInSeconds() {
        if (!isRunning) return

        val startTimeMillis = prefs.getLong(KEY_START_TIME, 0L)
        val currentTimeMillis = System.currentTimeMillis()
        val accumulatedTime = prefs.getLong(KEY_ACCUMULATED_TIME, 0L)

        if (startTimeMillis > 0) {
            val elapsedSeconds = (currentTimeMillis - startTimeMillis) / 1000
            _timeInSeconds.value = accumulatedTime + elapsedSeconds
        }
    }

    fun getCurrentTimeInSeconds(): Long {
        updateTimeInSeconds()
        return _timeInSeconds.value
    }

    fun isTimerRunning(): Boolean {
        return isRunning
    }

    fun formatTime(seconds: Long): String {
        val hrs = seconds / 3600
        val mins = (seconds % 3600) / 60
        val secs = seconds % 60
        return "%02d:%02d:%02d".format(hrs, mins, secs)
    }
}