package com.example.lessonapp.repository

import com.example.lessonapp.room.Note
import com.example.lessonapp.room.NoteDao
import javax.inject.Inject

class NoteRepository @Inject constructor(private val noteDao: NoteDao) {

    suspend fun insertNote(note: Note) {
        noteDao.insertNote(note)
    }

    suspend fun getNotesByLessonId(lessonId: Int): List<Note> {
        return noteDao.getNotesByLessonId(lessonId)
    }

    suspend fun getNoteById(noteId: Int): Note {
        return noteDao.getNoteById(noteId)
    }

    suspend fun updateNote(note: Note) {
        noteDao.updateNote(note)
    }
}