package com.alvarotc.swissknife.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Casino
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.ControlCamera
import androidx.compose.material.icons.outlined.FrontHand
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Numbers
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Toll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alvarotc.swissknife.R
import com.alvarotc.swissknife.model.ToolItem
import com.alvarotc.swissknife.navigation.NavRoutes
import com.alvarotc.swissknife.ui.components.ToolCard
import com.alvarotc.swissknife.ui.theme.AccentCoin
import com.alvarotc.swissknife.ui.theme.AccentColor
import com.alvarotc.swissknife.ui.theme.AccentDice
import com.alvarotc.swissknife.ui.theme.AccentEightBall
import com.alvarotc.swissknife.ui.theme.AccentFinger
import com.alvarotc.swissknife.ui.theme.AccentGroups
import com.alvarotc.swissknife.ui.theme.AccentList
import com.alvarotc.swissknife.ui.theme.AccentLottery
import com.alvarotc.swissknife.ui.theme.AccentPassword
import com.alvarotc.swissknife.ui.theme.AccentRPS
import com.alvarotc.swissknife.ui.theme.AccentRandom
import com.alvarotc.swissknife.ui.theme.AccentSanta
import com.alvarotc.swissknife.ui.theme.AccentTimer
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
            ToolItem(
                titleResId = R.string.tool_timer,
                icon = Icons.Outlined.Timer,
                accentColor = AccentTimer,
                route = NavRoutes.Timer.route,
            ),
            ToolItem(
                titleResId = R.string.tool_group_generator,
                icon = Icons.Outlined.Groups,
                accentColor = AccentGroups,
                route = NavRoutes.GroupGenerator.route,
            ),
            ToolItem(
                titleResId = R.string.tool_rock_paper_scissors,
                icon = Icons.Outlined.Circle,
                accentColor = AccentRPS,
                route = NavRoutes.RockPaperScissors.route,
            ),
            ToolItem(
                titleResId = R.string.tool_lottery,
                icon = Icons.Outlined.ConfirmationNumber,
                accentColor = AccentLottery,
                route = NavRoutes.Lottery.route,
            ),
            ToolItem(
                titleResId = R.string.tool_eight_ball,
                icon = Icons.Outlined.Psychology,
                accentColor = AccentEightBall,
                route = NavRoutes.EightBall.route,
            ),
            ToolItem(
                titleResId = R.string.tool_random_color,
                icon = Icons.Outlined.Palette,
                accentColor = AccentColor,
                route = NavRoutes.RandomColor.route,
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
