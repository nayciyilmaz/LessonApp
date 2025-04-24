package com.example.lessonapp.di

import android.content.Context
import androidx.room.Room
import com.example.lessonapp.room.LessonAppDatabase
import com.example.lessonapp.room.LessonDao
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
    fun providesDao(providesDatabase: LessonAppDatabase): LessonDao = providesDatabase.lessonDao()

    @Singleton
    @Provides
    fun providesDatabase(@ApplicationContext context: Context): LessonAppDatabase =
        Room.databaseBuilder(
            context = context,
            LessonAppDatabase::class.java,
            name = "items"
        ).fallbackToDestructiveMigration().build()
}