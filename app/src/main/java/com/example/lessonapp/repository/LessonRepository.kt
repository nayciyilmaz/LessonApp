package com.example.lessonapp.repository

import com.example.lessonapp.room.Item
import com.example.lessonapp.room.LessonDao
import javax.inject.Inject

class LessonRepository @Inject constructor(private val lessonDao: LessonDao) {

    suspend fun insertLesson(item: Item) {
        lessonDao.insertLesson(item)
    }

    suspend fun getAllLessons(): List<Item> {
        return lessonDao.getAllLessons()
    }

    suspend fun updateLesson(item: Item) {
        lessonDao.updateLesson(item)
    }
}