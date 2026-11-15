package com.parzij.recurra.ui.navigation

import android.graphics.Bitmap
import android.graphics.Color
import android.view.WindowInsetsController
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.test.platform.app.InstrumentationRegistry
import com.parzij.recurra.MainActivity
import java.io.File
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class MainActivityUiFlowTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    private fun tab(label: String) =
        compose.onNode(
            hasText(label) and SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab)
        )

    private fun back() {
        compose.waitForIdle()
        compose.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
    }

    @Test
    fun debugRegistrationLoginTabsAndSystemNavigation() {
        compose.onNodeWithText("Начать бесплатно").performScrollTo().performClick()
        compose.onNodeWithContentDescription("Имя").performTextInput("Мария")
        compose.onNodeWithContentDescription("Почта").performTextInput("demo@example.com")
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
        compose.onNodeWithText("Добрый день, Мария").assertExists()
        tab("Подписки").assertIsSelected()
        for (label in listOf("Подписки", "Списания", "Профиль")) {
            tab(label).performClick().assertIsSelected()
            for (useCancel in listOf(true, false)) {
                compose.onNodeWithContentDescription("Добавить подписку").performClick()
                compose.onNodeWithText("Новая подписка").assertIsDisplayed()
                compose.onNodeWithText("Отмена").assertExists()
                compose.onNodeWithContentDescription("Добавить подписку").assertDoesNotExist()
                tab("Подписки").assertDoesNotExist()
                if (useCancel) compose.onNodeWithText("Отмена").performScrollTo().performClick()
                else back()
                tab(label).assertIsSelected()
                compose.onNodeWithContentDescription("Добавить подписку").assertIsDisplayed()
            }
        }
        tab("Подписки").performClick().assertIsSelected()
        compose.onNodeWithText("Добрый день, Мария").assertExists()
        tab("Списания").performClick().assertIsSelected()
        compose.onNodeWithText("Ближайшие списания").assertExists()
        tab("Профиль").performClick().assertIsSelected()
        tab("Профиль").performClick()
        back()
        tab("Подписки").assertIsSelected()
        compose.onNodeWithText("Добрый день, Мария").assertExists()
        compose.onNodeWithContentDescription("Профиль").performClick()
        tab("Профиль").assertIsSelected()
        tab("Подписки").performClick().assertIsSelected()
        compose.activityRule.scenario.recreate()
        compose.onNodeWithText("Добрый день, Мария").assertExists()
        tab("Подписки").assertIsSelected().assertWidthIsEqualTo(110.dp).assertHeightIsEqualTo(65.dp)

        val window = compose.activity.window
        compose.runOnIdle {
            assertEquals(
                0,
                window.insetsController!!.systemBarsAppearance and
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
            )
            assertFalse(window.isNavigationBarContrastEnforced)
        }
        val panel =
            compose.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.SelectableGroup))
        panel.assertHeightIsEqualTo(85.dp)
        val navInset =
            ViewCompat.getRootWindowInsets(window.decorView)!!.getInsets(
                    WindowInsetsCompat.Type.navigationBars()
                )
                .bottom
        val rootBounds = compose.onRoot().fetchSemanticsNode().boundsInRoot
        val panelBounds = panel.fetchSemanticsNode().boundsInRoot
        compose.waitForIdle()
        val bitmap = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
        val plum = Color.rgb(85, 43, 91)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        File(context.cacheDir, "debug-ui-home-system.png").outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        assertEquals(plum, bitmap.getPixel(3, bitmap.height - 3))
        assertTrue("Gesture inset must remain below the tab controls", navInset > 0)
        assertEquals(rootBounds.bottom - navInset, panelBounds.bottom, 1f)
    }

    @Test
    fun directLoginAndCrossLinksReturnToWelcome() {
        compose.onNodeWithText("Войти").performScrollTo().performClick()
        compose.onNodeWithText("С возвращением").assertExists()
        compose.onNodeWithContentDescription("Назад").performScrollTo().performClick()
        compose.onNodeWithText("Начать бесплатно").performScrollTo().performClick()
        compose.onNodeWithText("Войти").performScrollTo().performClick()
        compose.onNodeWithText("С возвращением").assertExists()
        compose.onNodeWithText("Создать аккаунт").performScrollTo().performClick()
        compose.onNodeWithContentDescription("Имя").assertExists()
        back()
        compose.onNodeWithText("Начать бесплатно").assertExists()
    }
}
