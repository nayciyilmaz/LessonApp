package com.example.lessonapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lessonapp.screens.AddNoteScreen
import com.example.lessonapp.screens.LessonDetailsScreen
import com.example.lessonapp.screens.LessonsScreen
import com.example.lessonapp.screens.StatisticsScreen
import com.example.lessonapp.viewmodel.LessonAppViewModel

@Composable
fun LessonAppNavigation(
    viewModel: LessonAppViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController, startDestination = LessonAppScreen.LessonsScreen.route
    ) {
        composable(route = LessonAppScreen.LessonsScreen.route) {
            LessonsScreen(
                navController = navController,
                uiState = uiState,
                onAddLesson = { name ->
                    viewModel.addLesson(name)
                },
                onUpdateLessonName = { name ->
                    viewModel.updateLessonName(name)
                },
                inputLesson = viewModel.inputLesson
            )
        }
        composable(route = "Lesson Details Screen/{lessonName}") { backStackEntry ->
            val lessonName = backStackEntry.arguments?.getString("lessonName") ?: "Ders"
            LessonDetailsScreen(navController = navController, lessonName = lessonName)
        }
        composable(route = LessonAppScreen.AddNoteScreen.route) {
            AddNoteScreen(navController = navController)
        }
        composable(route = LessonAppScreen.StatisticsScreen.route) {
            StatisticsScreen(navController = navController)
        }
    }
}