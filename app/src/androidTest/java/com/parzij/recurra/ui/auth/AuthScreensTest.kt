package com.parzij.recurra.ui.auth

import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.test.platform.app.InstrumentationRegistry
import com.parzij.recurra.ui.auth.login.LoginScreen
import com.parzij.recurra.ui.auth.register.RegisterScreen
import com.parzij.recurra.ui.theme.RecurraTheme
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class AuthScreensTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    private fun capture(name: String) {
        compose.waitForIdle()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        File(context.cacheDir, "$name.png").outputStream().use {
            compose
                .onRoot()
                .captureToImage()
                .asAndroidBitmap()
                .compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        File(context.cacheDir, "$name.txt").writeText(compose.onRoot().printToString())
        val root = compose.onRoot().fetchSemanticsNode().boundsInRoot
        val logo = compose.onNodeWithContentDescription("Recurra").fetchSemanticsNode().boundsInRoot
        assertEquals(root.center.x, logo.center.x, 0.5f)
        val density = context.resources.displayMetrics.density
        val registering = name == "auth-register"
        val title =
            compose.onNode(
                hasText(if (registering) "Создать аккаунт" else "С возвращением") and
                    SemanticsMatcher.keyNotDefined(SemanticsActions.OnClick)
            )
        val bounds = title.fetchSemanticsNode().boundsInRoot
        assertEquals((if (registering) 45f else 152f) * density, bounds.top - logo.bottom, 1f)
        assertEquals(39f * density, bounds.left, 1f)
        assertEquals(root.width - 78f * density, bounds.width, 1f)
        val titleLayout = mutableListOf<TextLayoutResult>()
        title.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(titleLayout) }
        assertEquals(34.sp, titleLayout.single().layoutInput.style.fontSize)
        assertEquals(FontWeight.SemiBold, titleLayout.single().layoutInput.style.fontWeight)
        val subtitleLayout = mutableListOf<TextLayoutResult>()
        compose
            .onNodeWithText(
                if (registering) "Первый шаг к порядку" else "Ваши подписки - под контролем"
            )
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(subtitleLayout) }
        assertEquals(18.sp, subtitleLayout.single().layoutInput.style.fontSize)
        assertEquals(FontWeight.Medium, subtitleLayout.single().layoutInput.style.fontWeight)
    }

    @Test
    fun registrationFieldsPasswordsConsentAndSubmission() {
        var submitted: List<Any>? = null
        compose.activityRule.scenario.onActivity { it.enableEdgeToEdge() }
        compose.setContent {
            RecurraTheme {
                RegisterScreen(
                    onRegisterClick = { name, email, password, confirmation, accepted ->
                        submitted = listOf(name, email, password, confirmation, accepted)
                    }
                )
            }
        }
        capture("auth-register")
        compose.onNodeWithContentDescription("Имя").performTextInput("Анна")
        compose.onNodeWithContentDescription("Почта").performTextInput("anna@example.com")
        val password = compose.onNodeWithContentDescription("Пароль")
        val repeat = compose.onNodeWithContentDescription("Повтор пароля")
        password.performScrollTo().performTextInput("first-secret")
        repeat.performScrollTo().performTextInput("second-secret")
        compose.waitUntil(timeoutMillis = 5000) {
            ViewCompat.getRootWindowInsets(compose.activity.window.decorView)
                ?.isVisible(WindowInsetsCompat.Type.ime()) == true
        }
        repeat.assertIsDisplayed()
        password.assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Password))
        repeat.assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Password))
        compose.onNodeWithContentDescription("Показать: Пароль").performScrollTo().performClick()
        password.assertTextEquals("first-secret")
        repeat.assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Password))
        compose.onNodeWithContentDescription("Скрыть: Пароль").performClick()
        password.assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Password))
        compose
            .onNodeWithContentDescription("Показать: Повтор пароля")
            .performScrollTo()
            .performClick()
        repeat.assertTextEquals("second-secret")
        compose.onNodeWithContentDescription("Скрыть: Повтор пароля").performClick()
        repeat.performImeAction()
        val consent = compose.onNode(isToggleable())
        consent
            .performScrollTo()
            .assertIsOff()
            .performClick()
            .assertIsOn()
            .performClick()
            .assertIsOff()
        compose.onNodeWithText("Я соглашаюсь с", substring = true).performClick()
        consent.assertIsOn()
        compose
            .onNode(hasText("Создать аккаунт") and hasClickAction())
            .performScrollTo()
            .performClick()
        compose.runOnIdle {
            assertEquals(
                listOf("Анна", "anna@example.com", "first-secret", "second-secret", true),
                submitted,
            )
        }
    }

    @Test
    fun loginSupportsPasswordToggleAndImeSubmission() {
        var submitted: Pair<String, String>? = null
        compose.activityRule.scenario.onActivity { it.enableEdgeToEdge() }
        compose.setContent {
            RecurraTheme {
                LoginScreen(onLoginClick = { email, password -> submitted = email to password })
            }
        }
        capture("auth-login")
        compose.onNodeWithContentDescription("Почта").performTextInput("user@example.com")
        val password = compose.onNodeWithContentDescription("Пароль")
        password.performTextInput("secret")
        compose.onNodeWithContentDescription("Показать: Пароль").performClick()
        password.assertTextEquals("secret")
        compose.onNodeWithContentDescription("Скрыть: Пароль").performClick()
        password.performImeAction()
        compose.runOnIdle { assertEquals("user@example.com" to "secret", submitted) }
    }

    @Test
    fun flowSwitchesScreensRetainsInputAndGoesBack() {
        compose.activityRule.scenario.onActivity { it.enableEdgeToEdge() }
        compose.setContent { RecurraTheme { AuthFlow() } }
        compose.onNodeWithText("Начать бесплатно").performScrollTo().performClick()
        compose.onNodeWithContentDescription("Имя").performTextInput("Анна")
        compose.onNodeWithText("Войти").performScrollTo().performClick()
        compose.onNodeWithText("С возвращением").assertExists()
        compose.onNodeWithText("Создать аккаунт").performScrollTo().performClick()
        compose.onNodeWithContentDescription("Имя").assertTextEquals("Анна")
        compose.onNodeWithContentDescription("Назад").performScrollTo().performClick()
        compose.onNodeWithText("Начать бесплатно").assertExists()
        compose.onNodeWithText("Войти").performScrollTo().performClick()
        compose.onNodeWithText("С возвращением").assertExists()
        compose.waitForIdle()
        compose.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        compose.onNodeWithText("Начать бесплатно").assertExists()
    }

    @Test
    fun registrationStateSurvivesRestoration() {
        val restoration = StateRestorationTester(compose)
        restoration.setContent { RecurraTheme { RegisterScreen() } }
        compose.onNodeWithContentDescription("Имя").performTextInput("Анна")
        compose.onNodeWithContentDescription("Пароль").performScrollTo().performTextInput("secret")
        compose.onNodeWithContentDescription("Показать: Пароль").performClick()
        compose.onNode(isToggleable()).performScrollTo().performClick().assertIsOn()
        restoration.emulateSavedInstanceStateRestore()
        compose.onNodeWithContentDescription("Имя").assertTextEquals("Анна")

        compose
            .onNodeWithContentDescription("Пароль")
            .performScrollTo()
            .performTextInput("new-secret")
        compose.onNodeWithContentDescription("Пароль").assertTextEquals("new-secret")
        compose.onNodeWithContentDescription("Скрыть: Пароль").assertExists()
        compose.onNode(isToggleable()).assertIsOn()
    }
}
