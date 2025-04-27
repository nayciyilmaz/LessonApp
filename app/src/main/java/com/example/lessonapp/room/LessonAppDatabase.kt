package com.example.lessonapp.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Item::class, Note::class], version = 3, exportSchema = false)
abstract class LessonAppDatabase : RoomDatabase() {
    abstract fun lessonDao(): LessonDao
    abstract fun noteDao(): NoteDao
}