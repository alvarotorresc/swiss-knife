package com.alvarotc.swissknife.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.ControlCamera
import androidx.compose.material.icons.filled.Exposure
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FrontHand
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Toll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alvarotc.swissknife.R
import com.alvarotc.swissknife.model.ToolItem
import com.alvarotc.swissknife.navigation.NavRoutes
import com.alvarotc.swissknife.ui.components.ToolCard
import com.alvarotc.swissknife.ui.theme.AccentCoin
import com.alvarotc.swissknife.ui.theme.AccentCounter
import com.alvarotc.swissknife.ui.theme.AccentDice
import com.alvarotc.swissknife.ui.theme.AccentFinger
import com.alvarotc.swissknife.ui.theme.AccentLevel
import com.alvarotc.swissknife.ui.theme.AccentList
import com.alvarotc.swissknife.ui.theme.AccentPassword
import com.alvarotc.swissknife.ui.theme.AccentQR
import com.alvarotc.swissknife.ui.theme.AccentRandom
import com.alvarotc.swissknife.ui.theme.AccentSanta
import com.alvarotc.swissknife.ui.theme.AccentTimer
import com.alvarotc.swissknife.ui.theme.AccentTipCalc
import com.alvarotc.swissknife.ui.theme.AccentUnitConv
import com.alvarotc.swissknife.ui.theme.AccentWheel
import com.alvarotc.swissknife.ui.theme.DarkOnSurfaceVariant

@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    val tools =
        listOf(
            ToolItem(
                titleResId = R.string.tool_coin_flip,
                icon = Icons.Filled.Toll,
                accentColor = AccentCoin,
                route = NavRoutes.CoinFlip.route,
            ),
            ToolItem(
                titleResId = R.string.tool_dice_roll,
                icon = Icons.Filled.Casino,
                accentColor = AccentDice,
                route = NavRoutes.DiceRoll.route,
            ),
            ToolItem(
                titleResId = R.string.tool_random_number,
                icon = Icons.Filled.Numbers,
                accentColor = AccentRandom,
                route = NavRoutes.RandomNumber.route,
            ),
            ToolItem(
                titleResId = R.string.tool_secret_santa,
                icon = Icons.Filled.CardGiftcard,
                accentColor = AccentSanta,
                route = NavRoutes.SecretSanta.route,
            ),
            ToolItem(
                titleResId = R.string.tool_random_list,
                icon = Icons.Filled.FormatListBulleted,
                accentColor = AccentList,
                route = NavRoutes.RandomList.route,
            ),
            ToolItem(
                titleResId = R.string.tool_password_generator,
                icon = Icons.Filled.Key,
                accentColor = AccentPassword,
                route = NavRoutes.PasswordGenerator.route,
            ),
            ToolItem(
                titleResId = R.string.tool_counter,
                icon = Icons.Filled.Exposure,
                accentColor = AccentCounter,
                route = NavRoutes.Counter.route,
            ),
            ToolItem(
                titleResId = R.string.tool_timer,
                icon = Icons.Filled.Timer,
                accentColor = AccentTimer,
                route = NavRoutes.Timer.route,
            ),
            ToolItem(
                titleResId = R.string.tool_tip_calculator,
                icon = Icons.Filled.Payments,
                accentColor = AccentTipCalc,
                route = NavRoutes.TipCalculator.route,
            ),
            ToolItem(
                titleResId = R.string.tool_unit_converter,
                icon = Icons.Filled.SwapHoriz,
                accentColor = AccentUnitConv,
                route = NavRoutes.UnitConverter.route,
            ),
            ToolItem(
                titleResId = R.string.tool_level,
                icon = Icons.Filled.Layers,
                accentColor = AccentLevel,
                route = NavRoutes.Level.route,
            ),
            ToolItem(
                titleResId = R.string.tool_fortune_wheel,
                icon = Icons.Filled.ControlCamera,
                accentColor = AccentWheel,
                route = NavRoutes.FortuneWheel.route,
            ),
            ToolItem(
                titleResId = R.string.tool_finger_picker,
                icon = Icons.Filled.FrontHand,
                accentColor = AccentFinger,
                route = NavRoutes.FingerPicker.route,
            ),
            ToolItem(
                titleResId = R.string.tool_qr,
                icon = Icons.Filled.QrCode2,
                accentColor = AccentQR,
                route = NavRoutes.QRTool.route,
            ),
        )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item(span = { GridItemSpan(2) }) {
            Text(
                text = stringResource(R.string.pick_a_tool),
                color = DarkOnSurfaceVariant,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
            )
        }
        items(tools, key = { it.route }) { tool ->
            ToolCard(
                title = stringResource(tool.titleResId),
                icon = tool.icon,
                accentColor = tool.accentColor,
                onClick = { onNavigate(tool.route) },
            )
        }
    }
}
