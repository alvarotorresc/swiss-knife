package com.alvarotc.swissknife.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alvarotc.swissknife.R
import com.alvarotc.swissknife.ui.theme.AccentQR
import com.alvarotc.swissknife.ui.theme.DarkOnSurfaceVariant
import com.alvarotc.swissknife.ui.theme.DarkOutline
import com.alvarotc.swissknife.ui.theme.DarkSurfaceVariant
import com.alvarotc.swissknife.viewmodel.QRToolViewModel

@Composable
fun QRToolScreen(viewModel: QRToolViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val textFieldColors =
        OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = DarkOutline,
            focusedBorderColor = AccentQR,
            unfocusedLabelColor = DarkOnSurfaceVariant,
            focusedLabelColor = AccentQR,
            cursorColor = AccentQR,
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
        // Input
        OutlinedTextField(
            value = state.inputText,
            onValueChange = { viewModel.setInputText(it) },
            label = { Text(stringResource(R.string.text_to_encode)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            enabled = state.qrBitmap == null,
            maxLines = 3,
        )

        Spacer(modifier = Modifier.weight(1f))

        // QR Code display
        if (state.qrBitmap != null) {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(300.dp),
            ) {
                Image(
                    bitmap = state.qrBitmap!!.asImageBitmap(),
                    contentDescription = stringResource(R.string.qr_code),
                    modifier = Modifier.padding(16.dp),
                )
            }
        } else {
            Text(
                text = stringResource(R.string.qr_preview_here),
                color = DarkOnSurfaceVariant,
                fontSize = 14.sp,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Action buttons
        if (state.qrBitmap == null) {
            Button(
                onClick = { viewModel.generateQR() },
                enabled = state.inputText.trim().isNotBlank(),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentQR),
            ) {
                Text(
                    text = stringResource(R.string.generate_qr),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                )
            }
        } else {
            Button(
                onClick = { viewModel.clear() },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentQR),
            ) {
                Text(
                    text = stringResource(R.string.new_qr),
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                )
            }
        }
    }
}
