package com.alvarotc.swissknife.navigation

sealed class NavRoutes(val route: String) {
    data object Home : NavRoutes("home")

    data object CoinFlip : NavRoutes("coin_flip")

    data object DiceRoll : NavRoutes("dice_roll")

    data object RandomNumber : NavRoutes("random_number")

    data object SecretSanta : NavRoutes("secret_santa")
}
