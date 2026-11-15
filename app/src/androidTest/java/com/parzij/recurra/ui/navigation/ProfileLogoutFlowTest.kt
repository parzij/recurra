package com.parzij.recurra.ui.navigation

import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import com.parzij.recurra.ui.auth.AuthFlow
import com.parzij.recurra.ui.theme.RecurraTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ProfileLogoutFlowTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    private fun tab(label: String) =
        compose.onNode(
            hasText(label) and SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab)
        )

    private fun register(name: String, email: String) {
        compose.onNodeWithContentDescription("Имя").apply {
            performTextClearance()
            performTextInput(name)
        }
        compose.onNodeWithContentDescription("Почта").apply {
            performTextClearance()
            performTextInput(email)
        }
        compose
            .onNodeWithContentDescription("Пароль")
            .performScrollTo()
            .performTextInput("ui-only-pass")
        compose
            .onNodeWithContentDescription("Повтор пароля")
            .performScrollTo()
            .performTextInput("ui-only-pass")
        compose.onNodeWithContentDescription("Повтор пароля").performImeAction()
        compose.onNode(isToggleable()).performScrollTo().performClick()
        compose
            .onNode(hasText("Создать аккаунт") and hasClickAction())
            .performScrollTo()
            .performClick()
        tab("Подписки").assertIsSelected()
    }

    private fun login(email: String) {
        compose.onNodeWithContentDescription("Почта").apply {
            performTextClearance()
            performTextInput(email)
        }
        compose.onNodeWithContentDescription("Пароль").performScrollTo().apply {
            performTextClearance()
            performTextInput("ui-only-pass")
        }
        compose.onNodeWithContentDescription("Пароль").performImeAction()
        tab("Подписки").assertIsSelected()
    }

    @Test
    fun logoutClearsProtectedBackStackAndAnotherLoginUsesItsOwnIdentity() {
        var logoutCalls = 0
        var backReachedActivity = 0
        compose.activityRule.scenario.onActivity { activity ->
            activity.enableEdgeToEdge()

            activity.onBackPressedDispatcher.addCallback(
                activity,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        backReachedActivity++
                    }
                },
            )
        }
        compose.setContent { RecurraTheme { AuthFlow(onLogout = { logoutCalls++ }) } }
        compose.onNodeWithText("Начать бесплатно").performScrollTo().performClick()
        register("Александр", "alex@example.com")
        tab("Списания").performClick()
        tab("Профиль").performClick().assertIsSelected()
        compose.onNodeWithText("Александр").assertIsDisplayed()
        compose.onNodeWithText("alex@example.com").assertIsDisplayed()
        compose.onNodeWithText("Выйти из аккаунта").performClick()
        compose.onNodeWithText("С возвращением").assertIsDisplayed()
        compose
            .onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.SelectableGroup))
            .assertDoesNotExist()
        compose.runOnIdle { assertEquals(1, logoutCalls) }
        compose.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        compose.runOnIdle { assertEquals(1, backReachedActivity) }
        compose.onNodeWithText("С возвращением").assertIsDisplayed()

        login("other@example.com")
        tab("Профиль").performClick()
        compose.onNodeWithText("other@example.com").assertIsDisplayed()
        compose.onNodeWithText("Александр").assertDoesNotExist()
        compose.onNodeWithText("alex@example.com").assertDoesNotExist()
        compose.onNodeWithText("Выйти из аккаунта").performClick()
        compose.onNodeWithText("Создать аккаунт").performScrollTo().performClick()
        register("Мария", "maria@example.com")
        tab("Списания").performClick()
        tab("Профиль").performClick()
        compose.onNodeWithText("Мария").assertIsDisplayed()
        compose.onNodeWithText("maria@example.com").assertIsDisplayed()
        compose.onNodeWithText("Александр").assertDoesNotExist()
        compose.onNodeWithText("Выйти из аккаунта").performClick()
        compose.runOnIdle { assertEquals(3, logoutCalls) }
        compose.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        compose.runOnIdle { assertEquals(2, backReachedActivity) }
        compose.onNodeWithText("С возвращением").assertIsDisplayed()
        compose.onNodeWithContentDescription("Назад").performScrollTo().performClick()
        compose.onNodeWithText("Начать бесплатно").assertExists()
    }
}
