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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alvarotc.swissknife.R
import com.alvarotc.swissknife.ui.theme.AccentList
import com.alvarotc.swissknife.ui.theme.DarkOnSurfaceVariant
import com.alvarotc.swissknife.ui.theme.DarkOutline
import com.alvarotc.swissknife.ui.theme.DarkSurfaceVariant
import com.alvarotc.swissknife.viewmodel.RandomListError
import com.alvarotc.swissknife.viewmodel.RandomListViewModel

@Composable
fun RandomListScreen(viewModel: RandomListViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val textFieldColors =
        OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = DarkOutline,
            focusedBorderColor = AccentList,
            unfocusedLabelColor = DarkOnSurfaceVariant,
            focusedLabelColor = AccentList,
            cursorColor = AccentList,
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
        // Item input
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            OutlinedTextField(
                value = state.itemInput,
                onValueChange = { viewModel.setItemInput(it) },
                label = { Text(stringResource(R.string.item)) },
                singleLine = true,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors,
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { viewModel.addItem() }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add),
                    tint = AccentList,
                )
            }
        }

        // Error
        if (state.error != null) {
            Text(
                text =
                    when (state.error) {
                        RandomListError.ItemAlreadyAdded -> stringResource(R.string.error_item_already_added)
                        RandomListError.NeedMoreItems -> stringResource(R.string.error_need_more_items)
                        null -> ""
                    },
                color = Color(0xFFEF5350),
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp),
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Items count
        Text(
            text = stringResource(R.string.items_count, state.items.size),
            color = DarkOnSurfaceVariant,
            fontSize = 13.sp,
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Result or items list
        if (state.result != null) {
            // Show result
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(R.string.picked),
                    color = DarkOnSurfaceVariant,
                    fontSize = 16.sp,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = state.result ?: "",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentList,
                )
            }
        } else {
            // Show items list
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                itemsIndexed(state.items) { _, item ->
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
                                text = item,
                                color = Color.White,
                                modifier = Modifier.weight(1f),
                            )
                            IconButton(onClick = { viewModel.removeItem(item) }) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = stringResource(R.string.remove),
                                    tint = DarkOnSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action buttons
        if (state.result == null) {
            Button(
                onClick = { viewModel.pick() },
                enabled = state.items.size >= 2,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentList),
            ) {
                Text(
                    text = stringResource(R.string.pick),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                )
            }
        } else {
            Button(
                onClick = { viewModel.reset() },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentList),
            ) {
                Text(
                    text = stringResource(R.string.new_pick),
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                )
            }
            TextButton(onClick = { viewModel.reset() }) {
                Text(stringResource(R.string.cancel), color = DarkOnSurfaceVariant)
            }
        }
    }
}
