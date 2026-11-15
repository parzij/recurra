package com.parzij.recurra.ui.navigation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import com.parzij.recurra.ui.theme.RecurraTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class DisabledUiDemoTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun disabledDemoDoesNotAdvanceAfterFormSubmission() {
        var registerCalls = 0
        var loginCalls = 0
        compose.setContent {
            RecurraTheme {
                RecurraNavHost(
                    onRegister = { _, _, _, _, _ -> registerCalls++ },
                    onLogin = { _, _ -> loginCalls++ },
                    enableDebugUiDemo = false,
                )
            }
        }
        compose.onNodeWithText("Начать бесплатно").performScrollTo().performClick()
        compose
            .onNode(hasText("Создать аккаунт") and hasClickAction())
            .performScrollTo()
            .performClick()
        compose.onNodeWithContentDescription("Имя").assertExists()
        compose.runOnIdle { assertEquals(1, registerCalls) }
        compose.onNodeWithText("Войти").performScrollTo().performClick()
        compose.onNode(hasText("Войти") and hasClickAction()).performScrollTo().performClick()
        compose.onNodeWithText("С возвращением").assertExists()
        compose.runOnIdle { assertEquals(1, loginCalls) }
    }
}
