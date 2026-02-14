package com.alvarotc.swissknife

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alvarotc.swissknife.navigation.NavRoutes
import com.alvarotc.swissknife.ui.screens.CoinFlipScreen
import com.alvarotc.swissknife.ui.screens.DiceRollScreen
import com.alvarotc.swissknife.ui.screens.HomeScreen
import com.alvarotc.swissknife.ui.screens.RandomNumberScreen
import com.alvarotc.swissknife.ui.screens.SecretSantaScreen
import com.alvarotc.swissknife.ui.theme.DarkBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwissKnifeApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val title =
        when (currentRoute) {
            NavRoutes.CoinFlip.route -> "Coin Flip"
            NavRoutes.DiceRoll.route -> "Dice Roll"
            NavRoutes.RandomNumber.route -> "Random Number"
            NavRoutes.SecretSanta.route -> "Secret Santa"
            else -> "Swiss Knife"
        }

    val showBack = currentRoute != NavRoutes.Home.route

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (showBack) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                            )
                        }
                    }
                },
                colors =
                    TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = DarkBackground,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                    ),
            )
        },
        containerColor = DarkBackground,
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.Home.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    tween(300),
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    tween(300),
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    tween(300),
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    tween(300),
                )
            },
        ) {
            composable(NavRoutes.Home.route) {
                HomeScreen(
                    onNavigate = { route -> navController.navigate(route) },
                )
            }
            composable(NavRoutes.CoinFlip.route) {
                CoinFlipScreen()
            }
            composable(NavRoutes.DiceRoll.route) {
                DiceRollScreen()
            }
            composable(NavRoutes.RandomNumber.route) {
                RandomNumberScreen()
            }
            composable(NavRoutes.SecretSanta.route) {
                SecretSantaScreen()
            }
        }
    }
}
