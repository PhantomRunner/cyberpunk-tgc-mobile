package com.example.cyberpunktcg.data.local

import android.content.Context

internal class ThemePreferences(context: Context) {
    private val preferences = context.getSharedPreferences("theme_preferences", Context.MODE_PRIVATE)

    fun isDarkTheme(): Boolean = preferences.getBoolean("dark_theme", true)

    fun saveDarkTheme(isDarkTheme: Boolean) {
        // apply() updates memory immediately and writes to disk asynchronously.
        preferences.edit().putBoolean("dark_theme", isDarkTheme).apply()
    }
}
