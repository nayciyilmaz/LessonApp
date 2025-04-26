package com.example.lessonapp.di

import android.content.Context
import androidx.room.Room
import com.example.lessonapp.room.LessonAppDatabase
import com.example.lessonapp.room.LessonDao
import com.example.lessonapp.room.NoteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun providesLessonDao(database: LessonAppDatabase): LessonDao = database.lessonDao()

    @Singleton
    @Provides
    fun providesNoteDao(database: LessonAppDatabase): NoteDao = database.noteDao()

    @Singleton
    @Provides
    fun providesDatabase(@ApplicationContext context: Context): LessonAppDatabase =
        Room.databaseBuilder(
            context = context,
            LessonAppDatabase::class.java,
            "items"
        ).fallbackToDestructiveMigration().build()
}
