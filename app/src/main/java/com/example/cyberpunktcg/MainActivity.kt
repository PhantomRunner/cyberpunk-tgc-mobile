package com.example.cyberpunktcg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cyberpunktcg.navigation.CompanionApp
import com.example.cyberpunktcg.ui.theme.CyberpunkTcgTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CyberpunkTcgTheme (darkTheme = false){
                CompanionApp()
            }
        }
    }
}
