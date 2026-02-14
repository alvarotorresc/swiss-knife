package com.alvarotc.swissknife.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alvarotc.swissknife.R
import com.alvarotc.swissknife.ui.theme.DarkBackground
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun SplashScreen(onNavigateToHome: () -> Unit) {
    val logoOffset = remember { Animatable(300f) }
    val textAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Logo slide-in animation
        logoOffset.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 800),
        )

        // Text fade-in animation
        textAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600),
        )

        // Wait and navigate
        delay(2000)
        onNavigateToHome()
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(DarkBackground),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Filled.Construction,
                contentDescription = null,
                tint = Color.White,
                modifier =
                    Modifier
                        .size(120.dp)
                        .offset { IntOffset(0, logoOffset.value.roundToInt()) },
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.app_name),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = textAlpha.value),
            )
        }
    }
}
