package com.example.cyberpunktcg.ui.theme

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.cyberpunktcg.data.local.ThemePreferences

internal class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    private val preferences = ThemePreferences(application)

    var isDarkTheme by mutableStateOf(preferences.isDarkTheme())
        private set

    fun changeTheme(isDarkTheme: Boolean) {
        if (this.isDarkTheme == isDarkTheme) return

        preferences.saveDarkTheme(isDarkTheme)
        this.isDarkTheme = isDarkTheme
    }
}
