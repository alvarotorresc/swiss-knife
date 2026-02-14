package com.alvarotc.swissknife.navigation

sealed class NavRoutes(val route: String) {
    data object Splash : NavRoutes("splash")

    data object Home : NavRoutes("home")

    data object Settings : NavRoutes("settings")

    data object CoinFlip : NavRoutes("coin_flip")

    data object DiceRoll : NavRoutes("dice_roll")

    data object RandomNumber : NavRoutes("random_number")

    data object SecretSanta : NavRoutes("secret_santa")

    data object RandomList : NavRoutes("random_list")

    data object PasswordGenerator : NavRoutes("password_generator")

    data object Counter : NavRoutes("counter")

    data object Timer : NavRoutes("timer")

    data object TipCalculator : NavRoutes("tip_calculator")
}
