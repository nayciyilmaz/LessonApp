package com.example.lessonapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lessonapp.R
import com.example.lessonapp.components.DeleteConfirmationDialog
import com.example.lessonapp.components.EditButton
import com.example.lessonapp.components.EditTextField
import com.example.lessonapp.components.EditTopAppBar

@Composable
fun AddNoteScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    onStartClicked: () -> Unit,
    onResumeClicked: () -> Unit,
    onStopClicked: () -> Unit,
    isStartEnabled: Boolean,
    isResumeEnabled: Boolean,
    isStopEnabled: Boolean,
    formattedTime: String,
    inputSubject: String,
    inputExplanation: String,
    onUpdateSubjectName: (String) -> Unit,
    onUpdateExplanationName: (String) -> Unit,
    lessonId: Int,
    onSaveClicked: () -> Unit,
    onResetClicked: () -> Unit,
    isResetEnabled: Boolean,
    isEditingNote: Boolean = false,
    onCancelEditingNote: () -> Unit = {},
    showDeleteNoteDialog: Boolean = false,
    onShowDeleteNoteDialog: () -> Unit = {},
    onHideDeleteNoteDialog: () -> Unit = {},
    onDeleteNote: (Int) -> Unit = {}
) {
    if (showDeleteNoteDialog) {
        DeleteConfirmationDialog(
            title = stringResource(R.string.title_not_silme),
            message = stringResource(R.string.title_notsilme),
            onConfirm = { onDeleteNote(lessonId) },
            onDismiss = onHideDeleteNoteDialog
        )
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            EditTopAppBar(
                title = if (isEditingNote) stringResource(R.string.title_not_duzenle) else stringResource(
                    R.string.title_not_ekleme
                ),
                navController = navController,
                canNavigate = true
            )
        }
    ) { innerPadding ->
        Column(
            modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            HorizontalDivider(
                color = Color(0xFF00A2EA),
                thickness = 8.dp,
                modifier = modifier.fillMaxWidth()
            )

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier.padding(top = 12.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF97749B)),
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = formattedTime,
                            color = Color.White,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = modifier.padding(top = 24.dp, bottom = 24.dp)
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    EditButton(
                        icon = Icons.Filled.PlayArrow,
                        onClick = onStartClicked,
                        isEnabled = isStartEnabled,
                        modifier = Modifier.weight(1f)
                    )
                    EditButton(
                        icon = Icons.Filled.RestartAlt,
                        onClick = onResetClicked,
                        isEnabled = isResetEnabled,
                        modifier = Modifier.weight(1f)
                    )
                    EditButton(
                        icon = Icons.Filled.PlayCircle,
                        onClick = onResumeClicked,
                        isEnabled = isResumeEnabled,
                        modifier = Modifier.weight(1f)
                    )
                    EditButton(
                        icon = Icons.Filled.Stop,
                        onClick = onStopClicked,
                        isEnabled = isStopEnabled,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            HorizontalDivider(
                color = Color(0xFF03A9F4),
                thickness = 2.dp,
                modifier = modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 8.dp)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                EditTextField(
                    text = inputSubject,
                    onValueChange = { onUpdateSubjectName(it) },
                    label = stringResource(R.string.title_konu_basligi),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    )
                )
                EditTextField(
                    text = inputExplanation,
                    onValueChange = { onUpdateExplanationName(it) },
                    label = stringResource(R.string.title_calisma_detaylari),
                    singleLine = false,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    )
                )
                Spacer(modifier = Modifier.weight(1f))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp, bottom = 16.dp)
                ) {
                    Button(
                        onClick = { onSaveClicked() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isEditingNote)
                                stringResource(R.string.title_guncelle)
                            else
                                stringResource(R.string.title_kaydet),
                            modifier = modifier.padding(4.dp)
                        )
                    }

                    if (isEditingNote) {
                        Button(
                            onClick = { onShowDeleteNoteDialog() },
                            modifier = Modifier.fillMaxWidth().padding(4.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.title_notusil),
                                modifier = Modifier.padding(4.dp)
                            )
                        }

                        Button(
                            onClick = { onCancelEditingNote() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(R.string.title_iptal),
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}