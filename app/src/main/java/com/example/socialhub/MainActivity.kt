package com.example.socialhub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.example.socialhub.data.local.session.Auth0SessionStore
import com.example.socialhub.ui.SocialHubApp
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

// Main Android entry point. Hilt injects dependencies for composables via ViewModels.
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var auth0: Auth0

    @Inject
    lateinit var auth0SessionStore: Auth0SessionStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Auth0
        auth0 = Auth0.getInstance(
            getString(R.string.com_auth0_client_id),
            getString(R.string.com_auth0_domain)
        )

        // Opt into edge-to-edge so content can draw behind system bars.
        enableEdgeToEdge()
        setContent {
            // Root Compose tree for the whole app.
            SocialHubApp(
                onLoginClick = { login() },
                onLogoutClick = { logout() }
            )
        }
    }

    private fun login() {
        WebAuthProvider.login(auth0)
            .withScheme("https")
            .withScope("openid profile email offline_access")
            .start(this, object : Callback<Credentials, AuthenticationException> {
                override fun onSuccess(credentials: Credentials) {
                    lifecycleScope.launch {
                        auth0SessionStore.setLoggedIn(name = null, email = null)
                    }
                }

                override fun onFailure(exception: AuthenticationException) {
                    // Handle error cases
                }
            })
    }

    private fun logout() {
        WebAuthProvider.logout(auth0)
            .withScheme("https")
            .start(this, object : Callback<Void?, AuthenticationException> {
                override fun onSuccess(result: Void?) {
                    lifecycleScope.launch {
                        auth0SessionStore.clear()
                    }
                }

                override fun onFailure(exception: AuthenticationException) {
                    // Handle error
                }
            })
    }
}

