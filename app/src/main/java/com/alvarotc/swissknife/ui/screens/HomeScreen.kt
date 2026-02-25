package com.alvarotc.swissknife.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Casino
import androidx.compose.material.icons.outlined.ControlCamera
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.FrontHand
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Numbers
import androidx.compose.material.icons.outlined.Toll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alvarotc.swissknife.R
import com.alvarotc.swissknife.model.ToolItem
import com.alvarotc.swissknife.navigation.NavRoutes
import com.alvarotc.swissknife.ui.components.ToolCard
import com.alvarotc.swissknife.ui.theme.AccentCoin
import com.alvarotc.swissknife.ui.theme.AccentDice
import com.alvarotc.swissknife.ui.theme.AccentFinger
import com.alvarotc.swissknife.ui.theme.AccentList
import com.alvarotc.swissknife.ui.theme.AccentPassword
import com.alvarotc.swissknife.ui.theme.AccentRandom
import com.alvarotc.swissknife.ui.theme.AccentSanta
import com.alvarotc.swissknife.ui.theme.AccentWheel

@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    val tools =
        listOf(
            ToolItem(
                titleResId = R.string.tool_coin_flip,
                icon = Icons.Outlined.Toll,
                accentColor = AccentCoin,
                route = NavRoutes.CoinFlip.route,
            ),
            ToolItem(
                titleResId = R.string.tool_dice_roll,
                icon = Icons.Outlined.Casino,
                accentColor = AccentDice,
                route = NavRoutes.DiceRoll.route,
            ),
            ToolItem(
                titleResId = R.string.tool_random_number,
                icon = Icons.Outlined.Numbers,
                accentColor = AccentRandom,
                route = NavRoutes.RandomNumber.route,
            ),
            ToolItem(
                titleResId = R.string.tool_secret_santa,
                icon = Icons.Outlined.CardGiftcard,
                accentColor = AccentSanta,
                route = NavRoutes.SecretSanta.route,
            ),
            ToolItem(
                titleResId = R.string.tool_random_list,
                icon = Icons.AutoMirrored.Outlined.FormatListBulleted,
                accentColor = AccentList,
                route = NavRoutes.RandomList.route,
            ),
            ToolItem(
                titleResId = R.string.tool_fortune_wheel,
                icon = Icons.Outlined.ControlCamera,
                accentColor = AccentWheel,
                route = NavRoutes.FortuneWheel.route,
            ),
            ToolItem(
                titleResId = R.string.tool_finger_picker,
                icon = Icons.Outlined.FrontHand,
                accentColor = AccentFinger,
                route = NavRoutes.FingerPicker.route,
            ),
            ToolItem(
                titleResId = R.string.tool_password_generator,
                icon = Icons.Outlined.Key,
                accentColor = AccentPassword,
                route = NavRoutes.PasswordGenerator.route,
            ),
        )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(span = { GridItemSpan(2) }) {
            Text(
                text = stringResource(R.string.pick_a_tool),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
            )
        }
        itemsIndexed(tools, key = { _, it -> it.route }) { index, tool ->
            ToolCard(
                title = stringResource(tool.titleResId),
                icon = tool.icon,
                accentColor = tool.accentColor,
                onClick = { onNavigate(tool.route) },
                animationDelay = index * 60,
            )
        }
    }
}
