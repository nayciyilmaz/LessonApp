package com.example.lessonapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface LessonDao {

    @Insert
    suspend fun insertLesson(item: Item)

    @Query("SELECT * FROM items")
    suspend fun getAllLessons(): List<Item>

    @Update
    suspend fun updateLesson(item: Item)

    @Query("DELETE FROM items WHERE id = :lessonId")
    suspend fun deleteLesson(lessonId: Int)

    @Query("SELECT * FROM items WHERE id = :lessonId")
    suspend fun getLessonById(lessonId: Int): Item?

    @Query("SELECT * FROM items WHERE lessonName = :lessonName LIMIT 1")
    suspend fun getLessonByName(lessonName: String): Item?
}