package com.example.lessonapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lessonapp.repository.LessonRepository
import com.example.lessonapp.repository.NoteRepository
import com.example.lessonapp.room.Item
import com.example.lessonapp.room.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class LessonAppViewModel @Inject constructor(
    private val repository: LessonRepository,
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<List<Item>>(emptyList())
    val uiState: StateFlow<List<Item>> = _uiState.asStateFlow()

    private val _notesState = MutableStateFlow<List<Note>>(emptyList())
    val notesState: StateFlow<List<Note>> = _notesState.asStateFlow()

    var isEditingLesson by mutableStateOf(false)
        private set

    var isEditingNote by mutableStateOf(false)
        private set

    var currentEditingLessonId by mutableStateOf(0)
        private set

    var currentEditingNoteId by mutableStateOf(0)
        private set

    var inputLesson by mutableStateOf("")
        private set

    var inputSubject by mutableStateOf("")
        private set

    var inputExplanation by mutableStateOf("")
        private set

    var timeInSeconds by mutableStateOf(0L)
        private set

    val formattedTime: String
        get() = formatTime(timeInSeconds)

    var isStartEnabled by mutableStateOf(true)
    var isResumeEnabled by mutableStateOf(false)
    var isStopEnabled by mutableStateOf(false)
    var isResetEnabled by mutableStateOf(false)

    var showDeleteLessonDialog by mutableStateOf(false)
        private set

    var showDeleteNoteDialog by mutableStateOf(false)
        private set

    private var timerJob: Job? = null

    init {
        loadLessons()
    }

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

    fun startEditingNote(noteId: Int) {
        viewModelScope.launch {
            val note = noteRepository.getNoteById(noteId)
            inputSubject = note.subjectTitle
            inputExplanation = note.studyDetails
            timeInSeconds = note.studyTimeInMillis
            isEditingNote = true
            currentEditingNoteId = noteId

            isStartEnabled = false
            isResumeEnabled = true
            isStopEnabled = false
            isResetEnabled = false
        }
    }

    fun updateTimeManually(time: Long) {
        timeInSeconds = time
    }

    private fun loadLessons() {
        viewModelScope.launch {
            val items = repository.getAllLessons()
            _uiState.value = items
        }
    }

    fun onResetClicked() {
        timeInSeconds = 0L
        timerJob?.cancel()

        isStartEnabled = true
        isResumeEnabled = false
        isStopEnabled = false
        isResetEnabled = false
    }

    fun onStartClicked() {
        isStartEnabled = false
        isResumeEnabled = false
        isStopEnabled = true
        isResetEnabled = false
        startTimer()
    }

    fun onResumeClicked() {
        isResumeEnabled = false
        isStopEnabled = true
        isResetEnabled = false
        startTimer()
    }

    fun onStopClicked() {
        isStopEnabled = false
        isStartEnabled = false
        isResumeEnabled = true
        isResetEnabled = true
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

            if (isEditingNote) {
                val originalNote = noteRepository.getNoteById(currentEditingNoteId)
                val updatedNote = originalNote.copy(
                    subjectTitle = subjectText,
                    studyDetails = explanationText,
                    studyTimeInMillis = timeInSeconds
                )
                noteRepository.updateNote(updatedNote)
                isEditingNote = false
                currentEditingNoteId = 0
            } else {
                val note = Note(
                    lessonId = lessonId,
                    subjectTitle = subjectText,
                    studyDetails = explanationText,
                    studyTimeInMillis = timeInSeconds,
                    date = System.currentTimeMillis()
                )
                noteRepository.insertNote(note)
            }

            loadNotesByLessonId(lessonId)

            timeInSeconds = 0L
            inputSubject = ""
            inputExplanation = ""

            timerJob?.cancel()

            isStartEnabled = true
            isResumeEnabled = false
            isStopEnabled = false
            isResetEnabled = false
        }
    }

    fun cancelEditingNote() {
        isEditingNote = false
        currentEditingNoteId = 0
        timeInSeconds = 0L
        inputSubject = ""
        inputExplanation = ""

        isStartEnabled = true
        isResumeEnabled = false
        isStopEnabled = false
        isResetEnabled = false
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