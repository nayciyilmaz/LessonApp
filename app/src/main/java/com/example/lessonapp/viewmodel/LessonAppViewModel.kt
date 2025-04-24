package com.example.lessonapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lessonapp.repository.LessonRepository
import com.example.lessonapp.room.Item
import dagger.hilt.android.lifecycle.HiltViewModel
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

    init {
        loadLessons()
    }

    fun updateLessonName(lessonName: String) {
        inputLesson = lessonName
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
}
