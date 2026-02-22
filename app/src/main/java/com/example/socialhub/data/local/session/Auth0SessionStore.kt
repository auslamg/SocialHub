package com.example.socialhub.data.local.session

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * DataStore-backed store for Auth0 login status and profile metadata.
 */
private const val STORE_NAME = "auth0_session_store"
private val Context.dataStore by preferencesDataStore(name = STORE_NAME)
private val AUTH_LOGGED_IN = booleanPreferencesKey("auth_logged_in")
private val AUTH_NAME = stringPreferencesKey("auth_name")
private val AUTH_EMAIL = stringPreferencesKey("auth_email")

/**
 * Provides access to Auth0 session details used by the UI.
 */
class Auth0SessionStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Stream of the current Auth0 session state.
     */
    val authSession: Flow<AuthSession> = context.dataStore.data.map { prefs ->
        val loggedIn = prefs[AUTH_LOGGED_IN] ?: false
        val name = prefs[AUTH_NAME]
        val email = prefs[AUTH_EMAIL]
        AuthSession(isLoggedIn = loggedIn, name = name, email = email)
    }

    /**
     * Marks the session as logged in and stores optional name/email.
     */
    suspend fun setLoggedIn(name: String?, email: String?) {
        context.dataStore.edit { prefs ->
            prefs[AUTH_LOGGED_IN] = true
            if (name.isNullOrBlank()) {
                prefs.remove(AUTH_NAME)
            } else {
                prefs[AUTH_NAME] = name
            }
            if (email.isNullOrBlank()) {
                prefs.remove(AUTH_EMAIL)
            } else {
                prefs[AUTH_EMAIL] = email
            }
        }
    }

    /**
     * Clears Auth0 session data and marks the user as logged out.
     */
    suspend fun clear() {
        context.dataStore.edit { prefs ->
            prefs[AUTH_LOGGED_IN] = false
            prefs.remove(AUTH_NAME)
            prefs.remove(AUTH_EMAIL)
        }
    }
}

/**
 * Immutable snapshot of the Auth0 login state.
 */
data class AuthSession(
    val isLoggedIn: Boolean,
    val name: String?,
    val email: String?
)
