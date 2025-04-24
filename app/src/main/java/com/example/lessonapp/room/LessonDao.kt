package com.example.lessonapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LessonDao {

    @Insert
    suspend fun insertLesson(item: Item)

    @Query("SELECT * FROM items")
    suspend fun getAllLessons(): List<Item>
}