package com.parzij.recurra.ui.auth

import androidx.compose.runtime.Composable
import com.parzij.recurra.ui.navigation.RecurraNavHost
import com.parzij.recurra.ui.subscriptions.AddSubscriptionForm

@Composable
fun AuthFlow(
    onRegister:
        (
            name: String,
            email: String,
            password: String,
            confirmation: String,
            termsAccepted: Boolean,
        ) -> Unit =
        { _, _, _, _, _ ->
        },
    onLogin: (email: String, password: String) -> Unit = { _, _ -> },
    onLogout: () -> Unit = {},
    onAddSubscription: ((AddSubscriptionForm) -> Unit)? = null,
) {
    RecurraNavHost(
        onRegister = onRegister,
        onLogin = onLogin,
        onLogout = onLogout,
        onAddSubscription = onAddSubscription,
    )
}
