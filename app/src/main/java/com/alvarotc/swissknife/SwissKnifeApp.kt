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
import androidx.compose.ui.res.stringResource
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
import com.alvarotc.swissknife.ui.screens.SplashScreen
import com.alvarotc.swissknife.ui.theme.DarkBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwissKnifeApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val title =
        when (currentRoute) {
            NavRoutes.CoinFlip.route -> stringResource(R.string.tool_coin_flip)
            NavRoutes.DiceRoll.route -> stringResource(R.string.tool_dice_roll)
            NavRoutes.RandomNumber.route -> stringResource(R.string.tool_random_number)
            NavRoutes.SecretSanta.route -> stringResource(R.string.tool_secret_santa)
            else -> stringResource(R.string.app_name)
        }

    val showBack = currentRoute != NavRoutes.Home.route && currentRoute != NavRoutes.Splash.route
    val showTopBar = currentRoute != NavRoutes.Splash.route

    Scaffold(
        topBar = {
            if (showTopBar) {
                CenterAlignedTopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (showBack) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back),
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
            }
        },
        containerColor = DarkBackground,
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.Splash.route,
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
            composable(NavRoutes.Splash.route) {
                SplashScreen(
                    onNavigateToHome = {
                        navController.navigate(NavRoutes.Home.route) {
                            popUpTo(NavRoutes.Splash.route) { inclusive = true }
                        }
                    },
                )
            }
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
