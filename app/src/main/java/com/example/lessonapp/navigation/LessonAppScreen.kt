package com.example.lessonapp.navigation

enum class LessonAppScreen(val route: String) {
    AddNoteScreen("Add Note Screen"),
    LessonDetailsScreen("Lesson Details Screen/{lessonName}"),
    LessonsScreen("Lessons Screen"),
    StatisticsScreen("Statistics Screen")
}