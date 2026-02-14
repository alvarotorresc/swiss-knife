package com.alvarotc.swissknife

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.alvarotc.swissknife.ui.theme.SwissKnifeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SwissKnifeTheme {
                SwissKnifeApp()
            }
        }
    }
}
