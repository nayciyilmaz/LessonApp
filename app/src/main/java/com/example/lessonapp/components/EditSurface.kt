package com.example.lessonapp.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun EditSurface(
    modifier: Modifier = Modifier,
    title: String
) {
    Surface(
        modifier
            .padding(16.dp)
            .fillMaxWidth(),
        color = Color(0xFF2496EF)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = modifier.padding(top = 12.dp, bottom = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
    HorizontalDivider(
        color = Color(0xFF00A7F3),
        thickness = 2.dp,
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
    )
}