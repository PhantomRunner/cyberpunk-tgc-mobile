package com.example.cyberpunktcg

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.SideEffect
import androidx.lifecycle.ViewModelProvider
import com.example.cyberpunktcg.navigation.CompanionApp
import com.example.cyberpunktcg.ui.theme.CyberpunkTcgTheme
import com.example.cyberpunktcg.ui.theme.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val themeViewModel = ViewModelProvider(this)[ThemeViewModel::class.java]
        enableEdgeToEdge()
        setContent {
            val isDarkTheme = themeViewModel.isDarkTheme
            SideEffect {
                enableEdgeToEdge(
                    statusBarStyle = if (isDarkTheme) {
                        SystemBarStyle.dark(Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                    },
                    navigationBarStyle = if (isDarkTheme) {
                        SystemBarStyle.dark(Color.TRANSPARENT)
                    } else {
                        // Older Android versions need a dark scrim for white navigation icons.
                        SystemBarStyle.light(Color.TRANSPARENT, Color.BLACK)
                    },
                )
            }
            CyberpunkTcgTheme(darkTheme = isDarkTheme) {
                CompanionApp(
                    isDarkTheme = isDarkTheme,
                    onDarkThemeChange = themeViewModel::changeTheme,
                )
            }
        }
    }
}
