package com.example.lessonapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTopAppBar(
    title: String,
    navController: NavController,
    canNavigate: Boolean,
    icon: ImageVector? = null,
    iconDescription: String? = null,
    onIconClick: (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = TextStyle(
                    color = Color.Blue,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        },
        navigationIcon = {
            if (canNavigate) {
                Surface(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(45.dp),
                    shape = CircleShape,
                    color = Color.Transparent,
                    shadowElevation = 4.dp
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = Color.Blue
                        )
                    }
                }
            }
        },
        actions = {
            if (icon != null && onIconClick != null) {
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(45.dp)
                        .shadow(4.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = onIconClick) {
                        Icon(
                            imageVector = icon,
                            contentDescription = iconDescription,
                            tint = Color.Blue
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}
