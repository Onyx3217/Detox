package com.detox.core.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.detox.core.model.DetoxConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "detox_settings")

class ConfigRepository(
    private val context: Context
) {
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        encodeDefaults = true
    }

    private val configKey = stringPreferencesKey("detox_config_json")

    val configFlow: Flow<DetoxConfig> = context.dataStore.data.map { preferences ->
        val jsonString = preferences[configKey]
        if (jsonString != null) {
            try {
                json.decodeFromString<DetoxConfig>(jsonString)
            } catch (e: Exception) {
                DetoxConfig()
            }
        } else {
            DetoxConfig()
        }
    }

    suspend fun updateConfig(config: DetoxConfig) {
        val serialized = json.encodeToString(config)
        context.dataStore.edit { preferences ->
            preferences[configKey] = serialized
        }
    }

    fun exportConfigJson(config: DetoxConfig): String {
        return json.encodeToString(config)
    }

    fun importConfigJson(jsonString: String): DetoxConfig {
        return json.decodeFromString(jsonString)
    }
}
