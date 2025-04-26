package com.example.lessonapp.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val lessonId: Int,
    val subjectTitle: String,
    val studyDetails: String,
    val studyTimeInMillis: Long,
    val date: Long
)
