package com.parzij.recurra.ui.auth.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.parzij.recurra.R
import com.parzij.recurra.ui.auth.*
import com.parzij.recurra.ui.theme.RecurraTheme

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onRegisterClick:
        (
            name: String,
            email: String,
            password: String,
            confirmation: String,
            termsAccepted: Boolean,
        ) -> Unit =
        { _, _, _, _, _ ->
        },
) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmation by remember { mutableStateOf("") }
    var termsAccepted by rememberSaveable { mutableStateOf(false) }
    val focus = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    val next =
        KeyboardActions(onNext = { focus.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) })
    val done =
        KeyboardActions(
            onDone = {
                focus.clearFocus()
                keyboard?.hide()
            }
        )

    AuthLayout(
        title = R.string.auth_register_title,
        subtitle = R.string.auth_register_subtitle,
        footerPrompt = R.string.auth_register_footer,
        footerAction = R.string.welcome_login,
        onBackClick = onBackClick,
        onFooterClick = onLoginClick,
        logoToContentSpacing = 45.dp,
        modifier = modifier,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            AuthTextField(
                name,
                { name = it },
                R.string.auth_name,
                R.drawable.ic_user_outline_dark,
                keyboardOptions =
                    KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next,
                    ),
                keyboardActions = next,
            )
            AuthTextField(
                email,
                { email = it },
                R.string.auth_email,
                R.drawable.ic_sms,
                keyboardOptions =
                    KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                keyboardActions = next,
            )
            AuthPasswordField(
                password,
                { password = it },
                R.string.auth_password,
                imeAction = ImeAction.Next,
                keyboardActions = next,
            )
            AuthPasswordField(
                confirmation,
                { confirmation = it },
                R.string.auth_password_confirmation,
                keyboardActions = done,
            )
        }
        Spacer(Modifier.height(20.dp))
        AuthConsent(termsAccepted, { termsAccepted = it })
        Spacer(Modifier.height(20.dp))
        AuthButton(
            R.string.auth_register_title,
            onClick = {
                focus.clearFocus()
                keyboard?.hide()
                onRegisterClick(name, email, password, confirmation, termsAccepted)
            },
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    RecurraTheme { RegisterScreen() }
}
