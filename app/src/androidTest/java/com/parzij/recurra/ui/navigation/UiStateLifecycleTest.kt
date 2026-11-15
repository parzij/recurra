package com.parzij.recurra.ui.navigation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import com.parzij.recurra.MainActivity
import org.junit.Rule
import org.junit.Test

class UiStateLifecycleTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun demoChangesAndDraftSurviveTabsBackgroundAndActivityRecreation() {
        compose.onNodeWithText("Войти").performScrollTo().performClick()
        compose.onNodeWithContentDescription("Почта").performTextInput("state@example.com")
        compose.onNodeWithContentDescription("Пароль").performTextInput("ui-only-pass")
        compose.onNodeWithContentDescription("Пароль").performImeAction()
        compose.onNodeWithText("Профиль").performClick()
        compose.onNodeWithContentDescription("Добавить подписку").performClick()
        compose.onNodeWithContentDescription("Сервис").performTextInput("Lifecycle Test")
        compose
            .onNodeWithContentDescription("Сумма списания")
            .performScrollTo()
            .performTextInput("120")
        compose.onNodeWithContentDescription("Сумма списания").performImeAction()
        compose.onNodeWithContentDescription("Дата списания").performScrollTo().performClick()
        compose.onNode(hasText("15", substring = true) and hasClickAction()).performClick()
        compose.onNodeWithText("Выбрать").performClick()
        compose.onNodeWithText("3 месяца").performScrollTo().performClick()
        compose.onNodeWithText("Добавить подписку").performScrollTo().performClick()
        compose.onNodeWithText("Всего подписок").assertExists()
        compose.onNodeWithText("5").assertExists()
        compose.onNodeWithText("Подписки").performClick()
        compose.onNodeWithText("Lifecycle Test").performScrollTo().performTouchInput { longClick() }
        compose.onNodeWithContentDescription("Сервис").assertTextContains("Lifecycle Test")
        compose.onNodeWithText("3 месяца").assertIsSelected()
        compose.onNodeWithText("Приостановить").performScrollTo().performClick()
        compose.onNodeWithText("Списания").performClick()
        compose.onNodeWithText("Подписки").performClick()
        compose.onNodeWithText("Lifecycle Test").performScrollTo().performTouchInput { longClick() }
        compose.onNodeWithText("Возобновить").assertExists()
        compose.onNodeWithContentDescription("Сервис").performTextReplacement("Unsaved draft")
        compose.onNodeWithContentDescription("Сумма списания").performImeAction()
        compose.activityRule.scenario.moveToState(androidx.lifecycle.Lifecycle.State.CREATED)
        compose.activityRule.scenario.moveToState(androidx.lifecycle.Lifecycle.State.RESUMED)
        compose.activityRule.scenario.recreate()
        compose.onNodeWithContentDescription("Сервис").assertTextContains("Unsaved draft")
        compose.onNodeWithText("3 месяца").assertIsSelected()
        compose.onNodeWithText("Возобновить").assertExists()
        compose.activityRule.scenario.onActivity {
            it.requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        compose.waitUntil(5_000) {
            compose.activity.resources.configuration.orientation ==
                android.content.res.Configuration.ORIENTATION_LANDSCAPE
        }
        compose.onNodeWithContentDescription("Сервис").assertTextContains("Unsaved draft")
        compose.activityRule.scenario.onActivity {
            it.requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        compose.waitUntil(5_000) {
            compose.activity.resources.configuration.orientation ==
                android.content.res.Configuration.ORIENTATION_PORTRAIT
        }
        compose.onNodeWithContentDescription("Сервис").assertTextContains("Unsaved draft")
        compose.onNodeWithContentDescription("Закрыть редактирование").performClick()
        compose.waitUntil(5_000) {
            compose.onAllNodesWithText("Редактировать подписку").fetchSemanticsNodes().isEmpty()
        }
        compose.onNodeWithText("Lifecycle Test").performScrollTo().assertExists()
        compose.onNodeWithText("Профиль").performClick()
        compose.onNodeWithText("5").assertExists()
        compose.onNodeWithText("Выйти из аккаунта").performScrollTo().performClick()
        compose.onNodeWithText("С возвращением").assertExists()
        compose.onNodeWithContentDescription("Почта").performTextInput("second@example.com")
        compose.onNodeWithContentDescription("Пароль").performTextInput("ui-only-pass")
        compose.onNodeWithContentDescription("Пароль").performImeAction()
        compose.onNodeWithText("Lifecycle Test").assertDoesNotExist()
        compose.onNodeWithText("Профиль").performClick()
        compose.onNodeWithText("4").assertExists()
    }
}
