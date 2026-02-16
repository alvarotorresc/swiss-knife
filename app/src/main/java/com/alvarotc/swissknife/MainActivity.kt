package com.alvarotc.swissknife

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.alvarotc.swissknife.ui.theme.SwissKnifeTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            installSplashScreen()
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Smooth fade-in on recreation (language change)
        if (savedInstanceState != null) {
            window.decorView.alpha = 0f
            window.decorView.post {
                window.decorView.animate()
                    .alpha(1f)
                    .setDuration(350)
                    .start()
            }
        }

        setContent {
            SwissKnifeTheme {
                SwissKnifeApp()
            }
        }
    }

    override fun recreate() {
        // Smooth fade-out before recreation (language change)
        window.decorView.animate()
            .alpha(0f)
            .setDuration(250)
            .withEndAction { super.recreate() }
            .start()
    }
}
