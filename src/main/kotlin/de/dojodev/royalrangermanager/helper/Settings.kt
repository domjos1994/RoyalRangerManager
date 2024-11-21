package de.dojodev.royalrangermanager.helper

import java.util.prefs.Preferences

class Settings {
    private val preferences: Preferences = Preferences.userRoot()

    companion object {
        const val KEY_LAST_PROJECT = "LAST-PROJECT"
    }

    fun <T> saveSetting(key: String, value: T) {
        when(value) {
            is String -> this.preferences.put(key, value)
            is Double -> this.preferences.putDouble(key, value)
            is Float -> this.preferences.putFloat(key, value)
            is Long -> this.preferences.putLong(key, value)
            is Int -> this.preferences.putInt(key, value)
            is ByteArray -> this.preferences.putByteArray(key, value)
            is Boolean -> this.preferences.putBoolean(key, value)
            else -> this.preferences.put(key, value.toString())
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getSetting(key: String, default: T): T {
        return when(default) {
            is String -> this.preferences.get(key, default) as T
            is Double -> this.preferences.getDouble(key, default) as T
            is Float -> this.preferences.getFloat(key, default) as T
            is Long -> this.preferences.getLong(key, default) as T
            is Int -> this.preferences.getInt(key, default) as T
            is ByteArray -> this.preferences.getByteArray(key, default) as T
            is Boolean -> this.preferences.getBoolean(key, default) as T
            else -> this.preferences.get(key, "") as T
        }
    }
}