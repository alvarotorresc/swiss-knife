package com.alvarotc.swissknife.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alvarotc.swissknife.R
import com.alvarotc.swissknife.ui.theme.AccentTeamName
import com.alvarotc.swissknife.ui.theme.AccentTeamNameContainer
import com.alvarotc.swissknife.viewmodel.TeamNameViewModel

@Composable
fun TeamNameScreen(viewModel: TeamNameViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val adjectives = stringArrayResource(R.array.team_adjectives).toList()
    val nouns = stringArrayResource(R.array.team_nouns).toList()

    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(500),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "cursorAlpha",
    )

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (state.fullName.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.team_name_result),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(8.dp))

            val annotatedName =
                buildAnnotatedString {
                    val revealed =
                        state.fullName.substring(
                            0,
                            state.revealedChars.coerceAtMost(state.fullName.length),
                        )
                    val unrevealed =
                        state.fullName.substring(
                            state.revealedChars.coerceAtMost(state.fullName.length),
                        )

                    withStyle(SpanStyle(color = AccentTeamName)) {
                        append(revealed)
                    }
                    withStyle(SpanStyle(color = Color.Transparent)) {
                        append(unrevealed)
                    }
                    if (state.isGenerating) {
                        withStyle(SpanStyle(color = AccentTeamName.copy(alpha = cursorAlpha))) {
                            append("\u2588")
                        }
                    }
                }

            Text(
                text = annotatedName,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (state.history.isNotEmpty()) {
            Text(
                text = stringResource(R.string.previous_names),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
            ) {
                items(state.history) { name ->
                    Surface(
                        color = AccentTeamNameContainer,
                        shape = RoundedCornerShape(10.dp),
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clickable {
                                    val clipboard =
                                        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("team_name", name)
                                    clipboard.setPrimaryClip(clip)
                                },
                    ) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = AccentTeamName,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        Button(
            onClick = { viewModel.generate(adjectives, nouns) },
            enabled = !state.isGenerating,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = AccentTeamName,
                    disabledContainerColor = AccentTeamNameContainer,
                ),
        ) {
            Text(
                text =
                    if (state.isGenerating) {
                        stringResource(R.string.generating)
                    } else {
                        stringResource(R.string.generate_name)
                    },
                style = MaterialTheme.typography.labelLarge,
                color =
                    if (state.isGenerating) {
                        AccentTeamName
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
            )
        }
    }
}
