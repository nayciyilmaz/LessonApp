package com.example.lessonapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface NoteDao {

    @Insert
    suspend fun insertNote(note: Note)

    @Query("SELECT * FROM notes WHERE lessonId = :lessonId")
    suspend fun getNotesByLessonId(lessonId: Int): List<Note>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: Int): Note

    @Update
    suspend fun updateNote(note: Note)
}