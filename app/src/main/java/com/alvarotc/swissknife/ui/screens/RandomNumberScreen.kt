package com.alvarotc.swissknife.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alvarotc.swissknife.ui.theme.AccentRandom
import com.alvarotc.swissknife.ui.theme.DarkOnSurfaceVariant
import com.alvarotc.swissknife.ui.theme.DarkOutline
import com.alvarotc.swissknife.ui.theme.DarkSurfaceVariant
import com.alvarotc.swissknife.viewmodel.RandomNumberViewModel

@Composable
fun RandomNumberScreen(viewModel: RandomNumberViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val textFieldColors =
        OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = DarkOutline,
            focusedBorderColor = AccentRandom,
            unfocusedLabelColor = DarkOnSurfaceVariant,
            focusedLabelColor = AccentRandom,
            cursorColor = AccentRandom,
            unfocusedTextColor = Color.White,
            focusedTextColor = Color.White,
            unfocusedContainerColor = DarkSurfaceVariant,
            focusedContainerColor = DarkSurfaceVariant,
        )

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Range inputs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            OutlinedTextField(
                value = state.minText,
                onValueChange = { viewModel.setMin(it) },
                label = { Text("Min") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.width(140.dp),
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors,
            )
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedTextField(
                value = state.maxText,
                onValueChange = { viewModel.setMax(it) },
                label = { Text("Max") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.width(140.dp),
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors,
            )
        }

        // Error
        if (state.error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = state.error!!,
                color = Color(0xFFEF5350),
                fontSize = 13.sp,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Result
        Text(
            text = state.result?.toString() ?: "—",
            fontSize = 72.sp,
            fontWeight = FontWeight.Bold,
            color = if (state.result != null) AccentRandom else DarkOnSurfaceVariant,
        )

        Spacer(modifier = Modifier.weight(1f))

        // Generate button
        Button(
            onClick = { viewModel.generate() },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentRandom),
        ) {
            Text(
                text = "Generate",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
            )
        }
    }
}
