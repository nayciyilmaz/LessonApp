package com.example.lessonapp.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(
            entity = Item::class,
            parentColumns = ["id"],
            childColumns = ["lessonId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("lessonId")]
)
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val lessonId: Int,
    val subjectTitle: String,
    val studyDetails: String,
    val studyTimeInMillis: Long,
    val date: Long
)