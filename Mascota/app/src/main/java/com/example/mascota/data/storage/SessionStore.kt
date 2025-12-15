package com.example.mascota.data.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.mascota.data.model.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private val Context.dataStore by preferencesDataStore(name = "session_store")

class SessionStore(private val context: Context) {

    companion object {
        private val KEY_TOKEN = stringPreferencesKey("token")
        private val KEY_ROLE = stringPreferencesKey("role")
    }

    @Volatile
    private var cachedToken: String? = null

    val tokenFlow: Flow<String?> =
        context.dataStore.data.map { it[KEY_TOKEN] }

    val roleFlow: Flow<String?> =
        context.dataStore.data.map { it[KEY_ROLE] }

    suspend fun saveToken(token: String) {
        cachedToken = token
        context.dataStore.edit { prefs ->
            prefs[KEY_TOKEN] = token
        }
    }

    suspend fun saveRole(role: UserRole) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ROLE] = role.name
        }
    }

    suspend fun clear() {
        cachedToken = null
        context.dataStore.edit { prefs -> prefs.clear() }
    }

    fun getTokenSync(): String? = cachedToken ?: runBlocking {
        val t = tokenFlow.first()
        cachedToken = t
        t
    }
}
