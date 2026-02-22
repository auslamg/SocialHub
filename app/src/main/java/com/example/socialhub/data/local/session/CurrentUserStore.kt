package com.example.socialhub.data.local.session

import android.content.Context
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * DataStore-backed session store for the current app user id.
 */
private const val STORE_NAME = "current_user_store"
private val Context.dataStore by preferencesDataStore(name = STORE_NAME)
private val CURRENT_USER_ID = longPreferencesKey("current_user_id")

/**
 * Provides read/write access to the current user id used by the UI.
 */
class CurrentUserStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Stream of the current user id; null means "guest".
     */
    val currentUserId: Flow<Long?> = context.dataStore.data
        .map { preferences -> preferences[CURRENT_USER_ID] }

    /**
     * Persists the active user id for fast app startup.
     */
    suspend fun setCurrentUserId(userId: Long) {
        // Persist current user id for quick app startup lookup.
        context.dataStore.edit { preferences ->
            preferences[CURRENT_USER_ID] = userId
        }
    }

    /**
     * Clears the current user id (used on logout).
     */
    suspend fun clearCurrentUserId() {
        // Clears the session (used for logout).
        context.dataStore.edit { preferences ->
            preferences.remove(CURRENT_USER_ID)
        }
    }
}
