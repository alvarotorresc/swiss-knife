package com.alvarotc.swissknife.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alvarotc.swissknife.R
import com.alvarotc.swissknife.ui.theme.AccentPassword
import com.alvarotc.swissknife.ui.theme.DarkOnSurfaceVariant
import com.alvarotc.swissknife.ui.theme.DarkSurfaceVariant
import com.alvarotc.swissknife.viewmodel.PasswordError
import com.alvarotc.swissknife.viewmodel.PasswordGeneratorViewModel

@Composable
fun PasswordGeneratorScreen(viewModel: PasswordGeneratorViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),
    ) {
        // Length slider
        Text(
            text = stringResource(R.string.length_value, state.length),
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Slider(
            value = state.length.toFloat(),
            onValueChange = { viewModel.setLength(it.toInt()) },
            valueRange = 8f..64f,
            steps = 55,
            colors =
                SliderDefaults.colors(
                    thumbColor = AccentPassword,
                    activeTrackColor = AccentPassword,
                    inactiveTrackColor = DarkSurfaceVariant,
                ),
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Character type switches
        SwitchOption(
            label = stringResource(R.string.uppercase),
            checked = state.includeUppercase,
            onCheckedChange = { viewModel.toggleUppercase() },
        )
        Spacer(modifier = Modifier.height(12.dp))
        SwitchOption(
            label = stringResource(R.string.lowercase),
            checked = state.includeLowercase,
            onCheckedChange = { viewModel.toggleLowercase() },
        )
        Spacer(modifier = Modifier.height(12.dp))
        SwitchOption(
            label = stringResource(R.string.numbers_label),
            checked = state.includeNumbers,
            onCheckedChange = { viewModel.toggleNumbers() },
        )
        Spacer(modifier = Modifier.height(12.dp))
        SwitchOption(
            label = stringResource(R.string.symbols),
            checked = state.includeSymbols,
            onCheckedChange = { viewModel.toggleSymbols() },
        )

        // Error
        if (state.error != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text =
                    when (state.error) {
                        PasswordError.NoCharacterTypeSelected -> stringResource(R.string.error_select_at_least_one)
                        null -> ""
                    },
                color = Color(0xFFEF5350),
                fontSize = 13.sp,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Password display
        if (state.password != null) {
            Surface(
                color = DarkSurfaceVariant,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = state.password ?: "",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(
                        onClick = {
                            val clipboard =
                                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("password", state.password)
                            clipboard.setPrimaryClip(clip)
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ContentCopy,
                            contentDescription = stringResource(R.string.copy),
                            tint = AccentPassword,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Generate button
        Button(
            onClick = { viewModel.generate() },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentPassword),
        ) {
            Text(
                text = stringResource(R.string.generate),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
            )
        }
    }
}

@Composable
private fun SwitchOption(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors =
                SwitchDefaults.colors(
                    checkedThumbColor = AccentPassword,
                    checkedTrackColor = AccentPassword.copy(alpha = 0.5f),
                    uncheckedThumbColor = DarkOnSurfaceVariant,
                    uncheckedTrackColor = DarkSurfaceVariant,
                ),
        )
    }
}
