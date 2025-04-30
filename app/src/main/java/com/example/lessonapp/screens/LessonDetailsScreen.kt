package com.example.lessonapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lessonapp.R
import com.example.lessonapp.components.DeleteConfirmationDialog
import com.example.lessonapp.components.EditSurface
import com.example.lessonapp.components.EditTopAppBar
import com.example.lessonapp.navigation.LessonAppScreen
import com.example.lessonapp.room.Note
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LessonDetailsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    lessonName: String,
    lessonId: Int,
    notes: List<Note>,
    onStartEditingNote: (Int) -> Unit = {},
    showDeleteLessonDialog: Boolean = false,
    onShowDeleteLessonDialog: () -> Unit = {},
    onHideDeleteLessonDialog: () -> Unit = {},
    onDeleteLesson: (Int) -> Unit = {}
) {
    if (showDeleteLessonDialog) {
        DeleteConfirmationDialog(
            title = stringResource(R.string.title_ders_silme),
            message = stringResource(R.string.title_dersvenotsilme),
            onConfirm = { onDeleteLesson(lessonId) },
            onDismiss = onHideDeleteLessonDialog
        )
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            EditTopAppBar(
                title = lessonName,
                navController = navController,
                canNavigate = true,
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(45.dp)
                            .shadow(4.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = onShowDeleteLessonDialog) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = Color.Blue
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            HorizontalDivider(
                color = Color(0xFF03A9F4),
                thickness = 8.dp,
                modifier = modifier.fillMaxWidth()
            )
            EditSurface(
                title = stringResource(R.string.title_not_ekle),
                modifier = modifier.clickable {
                    navController.navigate("Add Note Screen/${lessonId}")
                }
            )

            val sortedNotes = notes.sortedByDescending { it.date }
            val groupedNotes = sortedNotes.groupBy { it.subjectTitle }

            LazyColumn(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                items(groupedNotes.keys.toList()) { subjectTitle ->
                    GroupedNoteItem(
                        subjectTitle = subjectTitle,
                        notes = groupedNotes[subjectTitle] ?: emptyList(),
                        onEditNote = { noteId ->
                            onStartEditingNote(noteId)
                            navController.navigate("Add Note Screen/${lessonId}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GroupedNoteItem(
    subjectTitle: String,
    notes: List<Note>,
    modifier: Modifier = Modifier,
    onEditNote: (Int) -> Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFA488A9)),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = subjectTitle,
                style = MaterialTheme.typography.titleMedium
            )

            notes.forEachIndexed { index, note ->
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(Date(note.date))
                val formattedTime = formatTime(note.studyTimeInMillis)

                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "${index + 1})",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = modifier.padding(end = 8.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Not: " + note.studyDetails,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "Süre: $formattedTime",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "Tarih: $formattedDate",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    IconButton(
                        onClick = { onEditNote(note.id) }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = null
                        )
                    }
                }
                if (index < notes.size - 1) {
                    HorizontalDivider(
                        color = Color.White,
                        thickness = 1.dp,
                        modifier = Modifier
                            .padding(top = 12.dp, bottom = 8.dp, start = 8.dp, end = 8.dp)
                            .fillMaxWidth()
                    )
                }
            }

            if (notes.size > 1) {
                val totalTime = notes.sumOf { it.studyTimeInMillis }
                val formattedTotalTime = formatTime(totalTime)

                HorizontalDivider(
                    color = Color.White,
                    thickness = 1.dp,
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 8.dp, start = 8.dp, end = 8.dp)
                        .fillMaxWidth()
                )

                Text(
                    text = "Toplam Süre: $formattedTotalTime",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

private fun formatTime(seconds: Long): String {
    val hrs = seconds / 3600
    val mins = (seconds % 3600) / 60
    val secs = seconds % 60
    return "%02d:%02d:%02d".format(hrs, mins, secs)
}