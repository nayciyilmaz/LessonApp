package com.example.lessonapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lessonapp.R
import com.example.lessonapp.components.EditTopAppBar

@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            EditTopAppBar(
                title = stringResource(R.string.title_istatistik),
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
        }
    }
}