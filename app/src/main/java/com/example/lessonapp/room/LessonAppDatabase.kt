package com.example.lessonapp.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Item::class], version = 2, exportSchema = false)
abstract class LessonAppDatabase : RoomDatabase() {
    abstract fun lessonDao(): LessonDao
}