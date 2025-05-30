package com.time.app.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey

private val Context.dataStore by preferencesDataStore(name = "settings")

object SettingsKeys {
    val AUTO_SYNC = booleanPreferencesKey("auto_sync")
}

class SettingsDataStore(private val context: Context) {

    val autoSyncFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[SettingsKeys.AUTO_SYNC] ?: true }

    suspend fun setAutoSync(enabled: Boolean) {
        context.dataStore.edit { settings ->
            settings[SettingsKeys.AUTO_SYNC] = enabled
        }
    }
}

