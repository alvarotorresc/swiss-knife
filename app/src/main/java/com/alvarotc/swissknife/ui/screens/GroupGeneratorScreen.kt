package com.alvarotc.swissknife.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alvarotc.swissknife.R
import com.alvarotc.swissknife.ui.theme.AccentGroups
import com.alvarotc.swissknife.ui.theme.AccentGroupsContainer
import com.alvarotc.swissknife.viewmodel.GroupGeneratorError
import com.alvarotc.swissknife.viewmodel.GroupGeneratorViewModel

private val GROUP_COLORS =
    listOf(
        AccentGroups,
        androidx.compose.ui.graphics.Color(0xFFF43F5E),
        androidx.compose.ui.graphics.Color(0xFF22C55E),
        androidx.compose.ui.graphics.Color(0xFFFBBF24),
        androidx.compose.ui.graphics.Color(0xFF3B82F6),
        androidx.compose.ui.graphics.Color(0xFF14B8A6),
        androidx.compose.ui.graphics.Color(0xFFFB923C),
        androidx.compose.ui.graphics.Color(0xFFEC4899),
        androidx.compose.ui.graphics.Color(0xFF8B5CF6),
        androidx.compose.ui.graphics.Color(0xFFEF4444),
    )

@Composable
fun GroupGeneratorScreen(viewModel: GroupGeneratorViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val textFieldColors =
        OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        )

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),
    ) {
        if (state.groups.isEmpty()) {
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
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { viewModel.addParticipant() }),
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { viewModel.addParticipant() }) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = stringResource(R.string.add),
                        tint = AccentGroups,
                    )
                }
            }

            if (state.error != null) {
                Text(
                    text =
                        when (state.error) {
                            GroupGeneratorError.NameAlreadyAdded ->
                                stringResource(R.string.error_name_already_added)
                            GroupGeneratorError.NeedMoreForGroups ->
                                stringResource(R.string.error_need_more_for_groups)
                            null -> ""
                        },
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.number_of_groups),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleSmall,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.setNumGroups(state.numGroups - 1) }) {
                        Icon(
                            imageVector = Icons.Outlined.Remove,
                            contentDescription = null,
                            tint = AccentGroups,
                        )
                    }
                    Text(
                        text = state.numGroups.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                    IconButton(onClick = { viewModel.setNumGroups(state.numGroups + 1) }) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = null,
                            tint = AccentGroups,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.participants_count, state.participants.size),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            if (state.groups.isEmpty()) {
                itemsIndexed(state.participants) { _, name ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
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
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f),
                            )
                            IconButton(onClick = { viewModel.removeParticipant(name) }) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = stringResource(R.string.remove),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            } else {
                state.groups.forEachIndexed { groupIndex, group ->
                    item(key = "header_$groupIndex") {
                        AnimatedVisibility(
                            visible = true,
                            enter =
                                slideInVertically(
                                    initialOffsetY = { it },
                                    animationSpec =
                                        spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessMediumLow,
                                        ),
                                ) +
                                    fadeIn(animationSpec = tween(300, delayMillis = groupIndex * 150)),
                        ) {
                            Text(
                                text = stringResource(R.string.group_label, groupIndex + 1),
                                style = MaterialTheme.typography.titleMedium,
                                color = GROUP_COLORS[groupIndex % GROUP_COLORS.size],
                                modifier = Modifier.padding(top = if (groupIndex > 0) 12.dp else 0.dp),
                            )
                        }
                    }
                    itemsIndexed(group, key = { i, name -> "g${groupIndex}_$i" }) { memberIndex, name ->
                        AnimatedVisibility(
                            visible = true,
                            enter =
                                scaleIn(
                                    initialScale = 0.6f,
                                    animationSpec =
                                        spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessMediumLow,
                                        ),
                                ) +
                                    fadeIn(
                                        animationSpec =
                                            tween(
                                                200,
                                                delayMillis = groupIndex * 150 + memberIndex * 80,
                                            ),
                                    ),
                        ) {
                            Surface(
                                color = AccentGroupsContainer,
                                shape = RoundedCornerShape(12.dp),
                            ) {
                                Text(
                                    text = name,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (state.groups.isEmpty()) {
            Button(
                onClick = { viewModel.generate() },
                enabled = state.participants.size > state.numGroups,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
            ) {
                Text(
                    text = stringResource(R.string.generate_groups),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = { viewModel.generate() },
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                        ),
                ) {
                    Text(
                        text = stringResource(R.string.generate_groups),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
            TextButton(onClick = { viewModel.reset() }) {
                Text(
                    text = stringResource(R.string.reset),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
