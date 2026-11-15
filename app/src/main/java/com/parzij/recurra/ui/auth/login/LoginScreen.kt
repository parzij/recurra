package com.parzij.recurra.ui.auth.login

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.parzij.recurra.R
import com.parzij.recurra.ui.auth.*
import com.parzij.recurra.ui.theme.RecurraTheme

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onLoginClick: (email: String, password: String) -> Unit = { _, _ -> },
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val focus = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    val submit = {
        focus.clearFocus()
        keyboard?.hide()
        onLoginClick(email, password)
    }
    AuthLayout(
        title = R.string.auth_login_title,
        subtitle = R.string.auth_login_subtitle,
        footerPrompt = R.string.auth_login_footer,
        footerAction = R.string.auth_register_title,
        onBackClick = onBackClick,
        onFooterClick = onRegisterClick,
        logoToContentSpacing = 152.dp,
        modifier = modifier,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            AuthTextField(
                email,
                { email = it },
                R.string.auth_email,
                R.drawable.ic_sms,
                keyboardOptions =
                    KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                keyboardActions =
                    KeyboardActions(
                        onNext = { focus.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }
                    ),
            )
            AuthPasswordField(
                password,
                { password = it },
                R.string.auth_password,
                keyboardActions = KeyboardActions(onDone = { submit() }),
            )
        }
        Spacer(Modifier.height(20.dp))
        AuthButton(R.string.welcome_login, submit)
    }
}

@Preview(showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    RecurraTheme { LoginScreen() }
}
