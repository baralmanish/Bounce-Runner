package com.example.bouncerunner.game

import android.content.Context
import android.content.SharedPreferences

data class Settings(
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val hapticEnabled: Boolean = true
)

class SettingsManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("game_settings", Context.MODE_PRIVATE)

    fun saveSettings(settings: Settings) {
        sharedPreferences.edit().apply {
            putBoolean("sound_enabled", settings.soundEnabled)
            putBoolean("music_enabled", settings.musicEnabled)
            putBoolean("haptic_enabled", settings.hapticEnabled)
            apply()
        }
    }

    fun loadSettings(): Settings {
        return Settings(
            soundEnabled = sharedPreferences.getBoolean("sound_enabled", true),
            musicEnabled = sharedPreferences.getBoolean("music_enabled", true),
            hapticEnabled = sharedPreferences.getBoolean("haptic_enabled", true)
        )
    }
}
