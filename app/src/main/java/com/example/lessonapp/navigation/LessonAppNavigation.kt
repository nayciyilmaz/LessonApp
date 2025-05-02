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
import com.example.lessonapp.viewmodel.StatisticsViewModel

@Composable
fun LessonAppNavigation(
    viewModel: LessonAppViewModel = hiltViewModel(),
    statisticsViewModel: StatisticsViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val notesState by viewModel.notesState.collectAsStateWithLifecycle()
    val dailyStats by statisticsViewModel.dailyStatistics.collectAsStateWithLifecycle()
    val weeklyStats by statisticsViewModel.weeklyStatistics.collectAsStateWithLifecycle()
    val monthlyStats by statisticsViewModel.monthlyStatistics.collectAsStateWithLifecycle()

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
                inputLesson = viewModel.inputLesson,
                isEditingLesson = viewModel.isEditingLesson,
                onStartEditingLesson = { id, name ->
                    viewModel.startEditingLesson(id, name)
                },
                onCancelEditingLesson = {
                    viewModel.cancelEditingLesson()
                }
            )
        }
        composable(route = "Lesson Details Screen/{lessonName}/{lessonId}") { backStackEntry ->
            val lessonName = java.net.URLDecoder.decode(
                backStackEntry.arguments?.getString("lessonName") ?: "Ders", "UTF-8"
            )
            val lessonId = backStackEntry.arguments?.getString("lessonId")?.toIntOrNull() ?: 0

            viewModel.loadNotesByLessonId(lessonId)

            LessonDetailsScreen(
                navController = navController,
                lessonName = lessonName,
                lessonId = lessonId,
                notes = notesState,
                onStartEditingNote = { noteId ->
                    viewModel.startEditingNote(noteId)
                },
                showDeleteLessonDialog = viewModel.showDeleteLessonDialog,
                onShowDeleteLessonDialog = { viewModel.showDeleteLessonDialog() },
                onHideDeleteLessonDialog = { viewModel.hideDeleteLessonDialog() },
                onDeleteLesson = { id ->
                    viewModel.deleteLesson(id)
                    navController.popBackStack()
                }
            )
        }
        composable(route = "Add Note Screen/{lessonId}") { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId")?.toIntOrNull() ?: 0

            AddNoteScreen(
                navController = navController,
                lessonId = lessonId,
                onStartClicked = { viewModel.onStartClicked() },
                onResumeClicked = { viewModel.onResumeClicked() },
                onStopClicked = { viewModel.onStopClicked() },
                onResetClicked = { viewModel.onResetClicked() },
                isStartEnabled = viewModel.isStartEnabled,
                isResumeEnabled = viewModel.isResumeEnabled,
                isStopEnabled = viewModel.isStopEnabled,
                isResetEnabled = viewModel.isResetEnabled,
                formattedTime = viewModel.formattedTime,
                inputSubject = viewModel.inputSubject,
                inputExplanation = viewModel.inputExplanation,
                onUpdateSubjectName = { viewModel.updateSubjectName(it) },
                onUpdateExplanationName = { viewModel.updateExplanationName(it) },
                isEditingNote = viewModel.isEditingNote,
                onCancelEditingNote = {
                    viewModel.cancelEditingNote()
                    navController.popBackStack()
                },
                onSaveClicked = {
                    viewModel.addNote(lessonId)
                    navController.popBackStack()
                },
                showDeleteNoteDialog = viewModel.showDeleteNoteDialog,
                onShowDeleteNoteDialog = { viewModel.showDeleteNoteDialog() },
                onHideDeleteNoteDialog = { viewModel.hideDeleteNoteDialog() },
                onDeleteNote = {
                    viewModel.deleteCurrentNote(lessonId)
                    navController.popBackStack()
                }
            )
        }
        composable(route = LessonAppScreen.StatisticsScreen.route) {
            StatisticsScreen(
                navController = navController,
                dailyStats = dailyStats,
                weeklyStats = weeklyStats,
                monthlyStats = monthlyStats
            )
        }
    }
}