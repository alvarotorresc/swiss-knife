package com.alvarotc.swissknife.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Toll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alvarotc.swissknife.navigation.NavRoutes
import com.alvarotc.swissknife.ui.components.ToolCard
import com.alvarotc.swissknife.ui.theme.AccentCoin
import com.alvarotc.swissknife.ui.theme.AccentDice
import com.alvarotc.swissknife.ui.theme.AccentRandom
import com.alvarotc.swissknife.ui.theme.AccentSanta
import com.alvarotc.swissknife.ui.theme.DarkOnSurfaceVariant

@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
    ) {
        Text(
            text = "Pick a tool",
            color = DarkOnSurfaceVariant,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(top = 8.dp, bottom = 20.dp),
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                ToolCard(
                    title = "Coin Flip",
                    icon = Icons.Filled.Toll,
                    accentColor = AccentCoin,
                    onClick = { onNavigate(NavRoutes.CoinFlip.route) },
                )
            }
            item {
                ToolCard(
                    title = "Dice Roll",
                    icon = Icons.Filled.Casino,
                    accentColor = AccentDice,
                    onClick = { onNavigate(NavRoutes.DiceRoll.route) },
                )
            }
            item {
                ToolCard(
                    title = "Random Number",
                    icon = Icons.Filled.Numbers,
                    accentColor = AccentRandom,
                    onClick = { onNavigate(NavRoutes.RandomNumber.route) },
                )
            }
            item {
                ToolCard(
                    title = "Secret Santa",
                    icon = Icons.Filled.CardGiftcard,
                    accentColor = AccentSanta,
                    onClick = { onNavigate(NavRoutes.SecretSanta.route) },
                )
            }
        }
    }
}
