package com.alvarotc.swissknife.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class ToolItem(
    val titleResId: Int,
    val icon: ImageVector,
    val accentColor: Color,
    val route: String,
)
