package com.alvarotc.swissknife.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alvarotc.swissknife.R
import com.alvarotc.swissknife.ui.theme.AccentSanta
import com.alvarotc.swissknife.ui.theme.AccentSantaContainer
import com.alvarotc.swissknife.ui.theme.DarkOnSurfaceVariant
import com.alvarotc.swissknife.ui.theme.DarkOutline
import com.alvarotc.swissknife.ui.theme.DarkSurfaceVariant
import com.alvarotc.swissknife.viewmodel.SecretSantaError
import com.alvarotc.swissknife.viewmodel.SecretSantaViewModel

@Composable
fun SecretSantaScreen(viewModel: SecretSantaViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val textFieldColors =
        OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = DarkOutline,
            focusedBorderColor = AccentSanta,
            unfocusedLabelColor = DarkOnSurfaceVariant,
            focusedLabelColor = AccentSanta,
            cursorColor = AccentSanta,
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
    ) {
        // Name input
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            OutlinedTextField(
                value = state.nameInput,
                onValueChange = { viewModel.setNameInput(it) },
                label = { Text(stringResource(R.string.name)) },
                singleLine = true,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors,
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { viewModel.addParticipant() }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add),
                    tint = AccentSanta,
                )
            }
        }

        // Error
        if (state.error != null) {
            Text(
                text =
                    when (state.error) {
                        SecretSantaError.NameAlreadyAdded -> stringResource(R.string.error_name_already_added)
                        SecretSantaError.NeedMoreParticipants -> stringResource(R.string.error_need_more_participants)
                        null -> ""
                    },
                color = Color(0xFFEF5350),
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp),
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Participants count
        Text(
            text = stringResource(R.string.participants_count, state.participants.size),
            color = DarkOnSurfaceVariant,
            fontSize = 13.sp,
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Participants list or assignments
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            if (state.assignments.isEmpty()) {
                // Show participant list
                itemsIndexed(state.participants) { _, name ->
                    Surface(
                        color = DarkSurfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = name,
                                color = Color.White,
                                modifier = Modifier.weight(1f),
                            )
                            IconButton(onClick = { viewModel.removeParticipant(name) }) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = stringResource(R.string.remove),
                                    tint = DarkOnSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            } else {
                // Show assignments (revealed progressively)
                itemsIndexed(state.assignments) { index, assignment ->
                    AnimatedVisibility(
                        visible = index < state.revealedCount,
                        enter = fadeIn(),
                    ) {
                        Surface(
                            color = AccentSantaContainer,
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = assignment.giver,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Text(
                                    text = "  →  ",
                                    color = AccentSanta,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = assignment.receiver,
                                    color = AccentSanta,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action buttons
        if (state.assignments.isEmpty()) {
            Button(
                onClick = { viewModel.draw() },
                enabled = state.participants.size >= 3,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentSanta),
            ) {
                Text(
                    text = stringResource(R.string.draw),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                )
            }
        } else {
            if (state.revealedCount < state.assignments.size) {
                Button(
                    onClick = { viewModel.revealNext() },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentSanta),
                ) {
                    Text(
                        text = stringResource(R.string.reveal_next),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                    )
                }
            } else {
                // All revealed — show share + reset
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(
                        onClick = {
                            val shareIntent =
                                Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, viewModel.buildShareText())
                                }
                            context.startActivity(
                                Intent.createChooser(shareIntent, context.getString(R.string.share_assignments)),
                            )
                        },
                        modifier =
                            Modifier
                                .weight(1f)
                                .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = stringResource(R.string.share),
                            tint = AccentSanta,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.share), color = AccentSanta)
                    }
                    Button(
                        onClick = { viewModel.reset() },
                        modifier =
                            Modifier
                                .weight(1f)
                                .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentSanta),
                    ) {
                        Text(
                            text = stringResource(R.string.new_draw),
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                        )
                    }
                }
            }

            if (state.revealedCount < state.assignments.size) {
                TextButton(onClick = { viewModel.reset() }) {
                    Text(stringResource(R.string.cancel), color = DarkOnSurfaceVariant)
                }
            }
        }
    }
}
