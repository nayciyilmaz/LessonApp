package com.example.lessonapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lessonapp.R
import com.example.lessonapp.components.EditSurface
import com.example.lessonapp.components.EditTopAppBar
import com.example.lessonapp.room.Item

@Composable
fun LessonsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    uiState: List<Item>,
    onAddLesson: (String) -> Unit,
    onUpdateLessonName: (String) -> Unit,
    inputLesson: String
) {
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            EditTopAppBar(
                title = stringResource(R.string.title_derslerim),
                navController = navController,
                canNavigate = false,
                canStatistic = true
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
                title = stringResource(R.string.title_ders_ekle),
                modifier = modifier.clickable {
                    showDialog = true
                }
            )

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(stringResource(R.string.title_ders_kaydi)) },
                    text = {
                        Column {
                            Text("Eklemek istediğiniz dersi girin.")
                            OutlinedTextField(
                                value = inputLesson,
                                onValueChange = {
                                    onUpdateLessonName(it)
                                },
                                label = { Text(stringResource(R.string.title_ders_ismi)) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            onAddLesson(inputLesson)
                            showDialog = false
                        }) {
                            Text(stringResource(R.string.title_onayla))
                        }
                    },
                    dismissButton = {
                        Button(onClick = {
                            showDialog = false
                        }) {
                            Text(stringResource(R.string.title_iptal))
                        }
                    }
                )
            }

            LazyColumn {
                items(uiState) { lesson ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFA488A9)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp, start = 12.dp, end = 12.dp)
                            .clickable {
                                val encodedName = java.net.URLEncoder.encode(lesson.name, "UTF-8")
                                navController.navigate("Lesson Details Screen/$encodedName")
                            }
                    ) {
                        Text(
                            text = lesson.name,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}


