package com.example.lessonapp.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lessonapp.repository.LessonRepository
import com.example.lessonapp.repository.NoteRepository
import com.example.lessonapp.room.Item
import com.example.lessonapp.room.Note
import com.example.lessonapp.timer.TimerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonAppViewModel @Inject constructor(
    private val repository: LessonRepository,
    private val noteRepository: NoteRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<List<Item>>(emptyList())
    val uiState: StateFlow<List<Item>> = _uiState.asStateFlow()

    private val _notesState = MutableStateFlow<List<Note>>(emptyList())
    val notesState: StateFlow<List<Note>> = _notesState.asStateFlow()

    private val timerManager = TimerManager(context)
    private var timeCollectorJob: Job? = null
    private var uiUpdateJob: Job? = null

    var isEditingLesson by mutableStateOf(false)
    var isEditingNote by mutableStateOf(false)
    var currentEditingLessonId by mutableStateOf(0)
    var currentEditingNoteId by mutableStateOf(0)

    var timeInSeconds by mutableStateOf(0L)
    var showDeleteLessonDialog by mutableStateOf(false)
    var showDeleteNoteDialog by mutableStateOf(false)

    var inputLesson by mutableStateOf("")
        private set

    var inputSubject by mutableStateOf("")
        private set

    var inputExplanation by mutableStateOf("")
        private set

    var isStartEnabled by mutableStateOf(true)
    var isResumeEnabled by mutableStateOf(false)
    var isStopEnabled by mutableStateOf(false)
    var isResetEnabled by mutableStateOf(false)

    init {
        loadLessons()
        startTimeCollector()
        startUiUpdater()
        updateButtonStates()
    }

    private fun startTimeCollector() {
        timeCollectorJob = viewModelScope.launch {
            timerManager.timeInSeconds.collect { seconds ->
                timeInSeconds = seconds
            }
        }
    }

    private fun startUiUpdater() {
        uiUpdateJob = viewModelScope.launch {
            while (true) {
                timerManager.updateTimeInSeconds()
                delay(500)
            }
        }
    }

    private fun updateButtonStates() {
        val isRunning = timerManager.isTimerRunning()
        if (isRunning) {
            isStartEnabled = false
            isResumeEnabled = false
            isStopEnabled = true
            isResetEnabled = false
        } else if (timeInSeconds > 0) {
            isStartEnabled = false
            isResumeEnabled = true
            isStopEnabled = false
            isResetEnabled = true
        } else {
            isStartEnabled = true
            isResumeEnabled = false
            isStopEnabled = false
            isResetEnabled = false
        }
    }

    val formattedTime: String
        get() = timerManager.formatTime(timeInSeconds)

    fun updateLessonName(lessonName: String) {
        inputLesson = lessonName
    }

    fun updateSubjectName(subjectName: String) {
        inputSubject = subjectName
    }

    fun updateExplanationName(explanationName: String) {
        inputExplanation = explanationName
    }

    fun addLesson(name: String) {
        if (name.isBlank()) return

        viewModelScope.launch {
            val uppercasedName = name.uppercase()
            val existingLesson = repository.getLessonByName(uppercasedName)

            if (existingLesson != null && !isEditingLesson) {
                return@launch
            }

            if (isEditingLesson) {
                val updatedItem = Item(id = currentEditingLessonId, lessonName = uppercasedName)
                repository.updateLesson(updatedItem)
                isEditingLesson = false
                currentEditingLessonId = 0
            } else {
                val item = Item(lessonName = uppercasedName)
                repository.insertLesson(item)
            }

            loadLessons()
            inputLesson = ""
        }
    }

    fun startEditingLesson(lessonId: Int, lessonName: String) {
        isEditingLesson = true
        currentEditingLessonId = lessonId
        inputLesson = lessonName
    }

    fun cancelEditingLesson() {
        isEditingLesson = false
        currentEditingLessonId = 0
        inputLesson = ""
    }

    override fun onCleared() {
        super.onCleared()
        timeCollectorJob?.cancel()
        uiUpdateJob?.cancel()
    }

    fun startEditingNote(noteId: Int) {
        viewModelScope.launch {
            val note = noteRepository.getNoteById(noteId)
            inputSubject = note.subjectTitle
            inputExplanation = note.studyDetails

            timerManager.pauseTimer()
            timerManager.startTimer(note.studyTimeInMillis)
            timerManager.pauseTimer()

            isEditingNote = true
            currentEditingNoteId = noteId

            updateButtonStates()
        }
    }

    fun updateTimeManually(time: Long) {
        timerManager.stopTimer()
        timerManager.startTimer(time)
        timerManager.pauseTimer()
        updateButtonStates()
    }

    private fun loadLessons() {
        viewModelScope.launch {
            val items = repository.getAllLessons()
            _uiState.value = items
        }
    }

    fun onResetClicked() {
        timerManager.stopTimer()
        updateButtonStates()
    }

    fun onStartClicked() {
        timerManager.startTimer(0)
        updateButtonStates()
    }

    fun onResumeClicked() {
        timerManager.startTimer(timeInSeconds)
        updateButtonStates()
    }

    fun onStopClicked() {
        timerManager.pauseTimer()
        updateButtonStates()
    }

    fun loadNotesByLessonId(lessonId: Int) {
        viewModelScope.launch {
            val notes = noteRepository.getNotesByLessonId(lessonId)
            _notesState.value = notes
        }
    }

    fun addNote(lessonId: Int) {
        viewModelScope.launch {
            val subjectText = if (inputSubject.isBlank()) "BULUNAMADI" else inputSubject.uppercase()
            val explanationText = if (inputExplanation.isBlank()) "BULUNAMADI" else inputExplanation

            val currentTime = timerManager.getCurrentTimeInSeconds()

            if (isEditingNote) {
                val originalNote = noteRepository.getNoteById(currentEditingNoteId)
                val updatedNote = originalNote.copy(
                    subjectTitle = subjectText,
                    studyDetails = explanationText,
                    studyTimeInMillis = currentTime
                )
                noteRepository.updateNote(updatedNote)
                isEditingNote = false
                currentEditingNoteId = 0
            } else {
                val note = Note(
                    lessonId = lessonId,
                    subjectTitle = subjectText,
                    studyDetails = explanationText,
                    studyTimeInMillis = currentTime,
                    date = System.currentTimeMillis()
                )
                noteRepository.insertNote(note)
            }

            loadNotesByLessonId(lessonId)
            timerManager.stopTimer()
            inputSubject = ""
            inputExplanation = ""
            updateButtonStates()
        }
    }

    fun cancelEditingNote() {
        isEditingNote = false
        currentEditingNoteId = 0
        timerManager.stopTimer()
        inputSubject = ""
        inputExplanation = ""

        updateButtonStates()
    }

    fun showDeleteLessonDialog() {
        showDeleteLessonDialog = true
    }

    fun hideDeleteLessonDialog() {
        showDeleteLessonDialog = false
    }

    fun showDeleteNoteDialog() {
        showDeleteNoteDialog = true
    }

    fun hideDeleteNoteDialog() {
        showDeleteNoteDialog = false
    }

    fun deleteLesson(lessonId: Int) {
        viewModelScope.launch {
            deleteNote()
            noteRepository.deleteNotesByLessonId(lessonId)
            repository.deleteLesson(lessonId)
            loadLessons()
            hideDeleteLessonDialog()
        }
    }

    fun deleteNote() {
        if (currentEditingNoteId <= 0) return

        viewModelScope.launch {
            noteRepository.deleteNote(currentEditingNoteId)

            val note = noteRepository.getNoteById(currentEditingNoteId)
            val lessonId = note.lessonId

            loadNotesByLessonId(lessonId)
            cancelEditingNote()
            hideDeleteNoteDialog()
        }
    }

    fun deleteCurrentNote(lessonId: Int) {
        if (currentEditingNoteId <= 0) return

        viewModelScope.launch {
            noteRepository.deleteNote(currentEditingNoteId)

            loadNotesByLessonId(lessonId)
            cancelEditingNote()
            hideDeleteNoteDialog()
        }
    }
}