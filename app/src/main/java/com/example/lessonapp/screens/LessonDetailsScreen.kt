package com.example.lessonapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lessonapp.R
import com.example.lessonapp.components.EditSurface
import com.example.lessonapp.components.EditTopAppBar
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
    notes: List<Note>
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            EditTopAppBar(
                title = lessonName,
                navController = navController,
                canNavigate = true,
                canStatistic = false
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

            LazyColumn(
                modifier = modifier.fillMaxWidth().padding(8.dp)
            ) {
                items(notes) { note ->
                    NoteItem(note = note)
                }
            }
        }
    }
}

@Composable
fun NoteItem(note: Note, modifier: Modifier = Modifier) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(note.date))
    val formattedTime = formatTime(note.studyTimeInMillis)

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
                text = note.subjectTitle,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = note.studyDetails,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Süre: $formattedTime",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall
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