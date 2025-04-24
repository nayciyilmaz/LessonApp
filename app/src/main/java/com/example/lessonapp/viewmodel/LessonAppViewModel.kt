package com.example.lessonapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lessonapp.repository.LessonRepository
import com.example.lessonapp.room.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonAppViewModel @Inject constructor(
    private val repository: LessonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<List<Item>>(emptyList())
    val uiState: StateFlow<List<Item>> = _uiState.asStateFlow()

    var inputLesson by mutableStateOf("")
        private set

    var inputSubject by mutableStateOf("")
        private set

    var inputExplanation by mutableStateOf("")
        private set

    var timeInSeconds by mutableStateOf(0L)


    val formattedTime: String
        get() = formatTime(timeInSeconds)

    var isStartEnabled by mutableStateOf(true)
    var isResumeEnabled by mutableStateOf(false)
    var isStopEnabled by mutableStateOf(false)

    private var timerJob: Job? = null

    init {
        loadLessons()
    }

    fun updateLessonName(lessonName: String) {
        inputLesson = lessonName
    }

    fun updateSubjectName(subjectName: String){
        inputSubject = subjectName
    }

    fun updateExplanationName(explanationName: String){
        inputExplanation = explanationName
    }

    fun addLesson(name: String) {
        if (name.isBlank()) return

        viewModelScope.launch {
            val uppercasedName = name.uppercase()
            val item = Item(name = uppercasedName)
            repository.insertLesson(item)
            loadLessons()
            inputLesson = ""
        }
    }

    private fun loadLessons() {
        viewModelScope.launch {
            val items = repository.getAllLessons()
            _uiState.value = items
        }
    }

    fun onStartClicked() {
        isStartEnabled = false
        isResumeEnabled = false
        isStopEnabled = true
        startTimer()
    }

    fun onResumeClicked() {
        isResumeEnabled = false
        isStopEnabled = true
        startTimer()
    }

    fun onStopClicked() {
        isStopEnabled = false
        isStartEnabled = false
        isResumeEnabled = true
        timerJob?.cancel()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                timeInSeconds++
            }
        }
    }

     fun formatTime(seconds: Long): String {
        val hrs = seconds / 3600
        val mins = (seconds % 3600) / 60
        val secs = seconds % 60
        return "%02d:%02d:%02d".format(hrs, mins, secs)
    }
}
