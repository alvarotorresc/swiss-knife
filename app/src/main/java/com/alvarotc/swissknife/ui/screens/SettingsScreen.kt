package com.alvarotc.swissknife.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alvarotc.swissknife.BuildConfig
import com.alvarotc.swissknife.R
import com.alvarotc.swissknife.ui.theme.DarkOnSurfaceVariant
import com.alvarotc.swissknife.ui.theme.DarkSurfaceVariant
import com.alvarotc.swissknife.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
    ) {
        // Language Section
        SectionTitle(stringResource(R.string.settings_language))
        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            color = DarkSurfaceVariant,
            shape = RoundedCornerShape(12.dp),
        ) {
            Column {
                LanguageOption(
                    label = "English",
                    code = "en",
                    selected = state.currentLanguage == "en",
                    onSelect = { viewModel.setLanguage("en") },
                )
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                LanguageOption(
                    label = "Español",
                    code = "es",
                    selected = state.currentLanguage == "es",
                    onSelect = { viewModel.setLanguage("es") },
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // About Section
        SectionTitle(stringResource(R.string.settings_about))
        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            color = DarkSurfaceVariant,
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                InfoRow(stringResource(R.string.settings_version), BuildConfig.VERSION_NAME)
                Spacer(modifier = Modifier.height(12.dp))
                InfoRow(stringResource(R.string.settings_developer), "Álvaro TC")
                Spacer(modifier = Modifier.height(12.dp))
                ClickableInfoRow(
                    label = "GitHub",
                    value = "alvarotorresc/swiss-knife-android",
                    onClick = {
                        val intent =
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://github.com/alvarotorresc/swiss-knife-android"),
                            )
                        context.startActivity(intent)
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Privacy Section
        SectionTitle(stringResource(R.string.settings_privacy))
        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            color = DarkSurfaceVariant,
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.settings_privacy_description),
                    color = Color.White,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
    )
}

@Composable
private fun LanguageOption(
    label: String,
    code: String,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onSelect)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f),
        )
        RadioButton(
            selected = selected,
            onClick = onSelect,
            colors =
                RadioButtonDefaults.colors(
                    selectedColor = Color.White,
                    unselectedColor = DarkOnSurfaceVariant,
                ),
        )
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
) {
    Column {
        Text(
            text = label,
            color = DarkOnSurfaceVariant,
            fontSize = 12.sp,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = Color.White,
            fontSize = 16.sp,
        )
    }
}

@Composable
private fun ClickableInfoRow(
    label: String,
    value: String,
    onClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
    ) {
        Text(
            text = label,
            color = DarkOnSurfaceVariant,
            fontSize = 12.sp,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = Color(0xFF42A5F5),
            fontSize = 16.sp,
        )
    }
}
