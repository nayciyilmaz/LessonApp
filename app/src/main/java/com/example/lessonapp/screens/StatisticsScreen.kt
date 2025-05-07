package com.example.lessonapp.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lessonapp.R
import com.example.lessonapp.components.EditTopAppBar
import com.example.lessonapp.model.DailyStatistic
import com.example.lessonapp.model.MonthlyStatistic
import com.example.lessonapp.model.WeeklyStatistic

@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    dailyStats: List<DailyStatistic>,
    weeklyStats: List<WeeklyStatistic>,
    monthlyStats: List<MonthlyStatistic>
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            EditTopAppBar(
                title = stringResource(R.string.title_istatistik),
                navController = navController,
                canNavigate = true
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            HorizontalDivider(
                color = Color(0xFF03A9F4),
                thickness = 8.dp,
                modifier = modifier.fillMaxWidth()
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    StatisticCard(
                        title = stringResource(R.string.title_günlük),
                        content = { DailyStatisticsContent(dailyStats) }
                    )
                }

                item {
                    StatisticCard(
                        title = stringResource(R.string.title_haftalık),
                        content = { WeeklyStatisticsContent(weeklyStats) }
                    )
                }

                item {
                    StatisticCard(
                        title = stringResource(R.string.title_aylık),
                        content = { MonthlyStatisticsContent(monthlyStats) }
                    )
                }
            }
        }
    }
}

@Composable
fun StatisticCard(
    title: String,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "rotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFA488A9))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    modifier = Modifier
                        .rotate(rotationState)
                        .size(24.dp)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
fun DailyStatisticsContent(dailyStats: List<DailyStatistic>) {
    if (dailyStats.isEmpty()) {
        return
    }
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        dailyStats.forEach { dailyStat ->
            DailyStatItem(dailyStat)
        }
    }
}

@Composable
fun DailyStatItem(dailyStat: DailyStatistic) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = dailyStat.date + " " + dailyStat.dayName,
            )
        }

        dailyStat.lessonTimes.forEach { (lesson, time) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = lesson)
                Text(text = time)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.title_toplam),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = dailyStat.totalTime,
                fontWeight = FontWeight.Bold
            )
        }

        HorizontalDivider(
            color = Color.White,
            thickness = 1.dp,
            modifier = Modifier
                .padding(top = 12.dp, bottom = 12.dp, start = 8.dp, end = 8.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
fun WeeklyStatisticsContent(weeklyStats: List<WeeklyStatistic>) {
    if (weeklyStats.isEmpty()) {
        return
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        weeklyStats.forEach { weeklyStat ->
            WeeklyStatItem(weeklyStat = weeklyStat)
        }
    }
}

@Composable
fun WeeklyStatItem(
    weeklyStat: WeeklyStatistic
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${weeklyStat.startDate} - ${weeklyStat.endDate}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        if (weeklyStat.lessonTimes.isNotEmpty()) {
            weeklyStat.lessonTimes.forEach { (lesson, time) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = lesson)
                    Text(text = time)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.title_haftalik_toplam),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = weeklyStat.totalTime,
                fontWeight = FontWeight.Bold
            )
        }

        HorizontalDivider(
            color = Color.White,
            thickness = 1.dp,
            modifier = Modifier
                .padding(top = 12.dp, bottom = 12.dp, start = 8.dp, end = 8.dp)
                .fillMaxWidth()
        )
    }
}


@Composable
fun MonthlyStatisticsContent(monthlyStats: List<MonthlyStatistic>) {
    if (monthlyStats.isEmpty()) {
        return
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        monthlyStats.forEach { monthlyStat ->
            MonthlyStatItem(monthlyStat)
        }
    }
}

@Composable
fun MonthlyStatItem(monthlyStat: MonthlyStatistic) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = monthlyStat.month,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.title_aylik_toplam),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = monthlyStat.totalTime,
                fontWeight = FontWeight.Bold
            )
        }
        HorizontalDivider(
            color = Color.White,
            thickness = 1.dp,
            modifier = Modifier
                .padding(top = 12.dp, bottom = 12.dp, start = 8.dp, end = 8.dp)
                .fillMaxWidth()
        )
    }
}