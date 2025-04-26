package com.example.lessonapp.navigation

enum class LessonAppScreen(val route: String) {
    AddNoteScreen("Add Note Screen/{lessonId}"),
    LessonDetailsScreen("Lesson Details Screen/{lessonName}/{lessonId}"),
    LessonsScreen("Lessons Screen"),
    StatisticsScreen("Statistics Screen")
}