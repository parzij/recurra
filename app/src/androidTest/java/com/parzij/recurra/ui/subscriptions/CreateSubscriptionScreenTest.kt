package com.parzij.recurra.ui.subscriptions

import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.test.platform.app.InstrumentationRegistry
import com.parzij.recurra.ui.theme.RecurraTheme
import java.io.File
import java.time.LocalDate
import java.util.TimeZone
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class CreateSubscriptionScreenTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun rubleSuffixRemainsOutsideEditableAmount() {
        compose.setContent { RecurraTheme { CreateSubscriptionScreen(onCancel = {}) } }
        val amount = compose.onNodeWithContentDescription("Сумма списания")
        compose.onNodeWithText("₽", useUnmergedTree = true).assertExists()
        amount.performScrollTo().performClick().performTextInput("120")
        amount.assertTextContains("120")
        compose.onNodeWithText(" ₽", useUnmergedTree = true).assertExists()
        amount.performTextInputSelection(TextRange(1))
        amount.performTextInput("9")
        amount.assertTextContains("1920")
        amount.performTextClearance()
        amount.performKeyInput { pressKey(Key.Backspace) }
        amount.assertTextContains("")
        compose.onNodeWithText("₽", useUnmergedTree = true).assertExists()
    }

    @Test
    fun russianCalendarStartsOnMonday() {
        val form = mutableStateOf(AddSubscriptionForm(date = LocalDate.of(2026, 11, 4)))
        compose.setContent {
            RecurraTheme {
                AddSubscriptionContent(
                    form.value,
                    {},
                    {},
                    { form.value = form.value.copy(date = it) },
                    {},
                    {},
                    {},
                )
            }
        }
        compose.onNodeWithContentDescription("Дата списания").performScrollTo().performClick()
        compose.waitForIdle()
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        android.os.SystemClock.sleep(250)
        InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot().let { bitmap ->
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            File(context.cacheDir, "add-calendar-ru.png").outputStream().use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        }
        val headers =
            listOf(
                    "понедельник",
                    "вторник",
                    "среда",
                    "четверг",
                    "пятница",
                    "суббота",
                    "воскресенье",
                )
                .map {
                    compose
                        .onNodeWithContentDescription(it, useUnmergedTree = true)
                        .fetchSemanticsNode()
                        .boundsInRoot
                }
        headers.zipWithNext().forEach { (a, b) ->
            assertTrue(a.left < b.left)
            assertEquals(a.top, b.top, 1f)
        }
        compose.onNodeWithText("Выберите дату").assertExists()
        val firstDay =
            compose
                .onNode(hasText("Воскресенье, 1 ноября", substring = true) and hasClickAction())
                .fetchSemanticsNode()
                .boundsInRoot
        assertEquals(headers.last().center.x, firstDay.center.x, 1f)
        compose.onNodeWithContentDescription("Перейти к следующему месяцу").performClick()
        compose.onNodeWithContentDescription("Перейти к предыдущему месяцу").performClick()
        compose
            .onNodeWithContentDescription("Перейти к выбору года", substring = true)
            .performClick()
        compose.onNode(hasText("2027", substring = true) and hasClickAction()).performClick()
        compose.onNode(hasText("15", substring = true) and hasClickAction()).performClick()
        compose.onNodeWithText("Выбрать").performClick()
        compose.onNodeWithText("15.11.2027").assertExists()
    }

    @Test
    fun validationInputPeriodsDateAndSubmit() {
        var result: AddSubscriptionForm? = null
        var cancelled = false
        val previousZone = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"))
        try {
            compose.setContent {
                RecurraTheme {
                    CreateSubscriptionScreen(
                        onCancel = { cancelled = true },
                        onAddSubscription = { result = it },
                    )
                }
            }
            compose.onNodeWithText("Добавить подписку").performScrollTo().performClick()
            compose.onNodeWithText("Введите название сервиса").assertExists()
            compose.onNodeWithText("Введите сумму больше нуля").assertExists()
            compose.onNodeWithText("Выберите дату списания").assertExists()
            assertNull(result)
            compose
                .onNodeWithContentDescription("Сервис")
                .performScrollTo()
                .performTextInput("YouTube Премиум")
            compose
                .onNodeWithContentDescription("Сумма списания")
                .performScrollTo()
                .performTextInput("299,50")
            compose.onNodeWithContentDescription("Сумма списания").performImeAction()
            compose.waitUntil(timeoutMillis = 5_000) {
                ViewCompat.getRootWindowInsets(compose.activity.window.decorView)
                    ?.isVisible(WindowInsetsCompat.Type.ime()) != true
            }
            compose
                .onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.SelectableGroup))
                .assertHeightIsEqualTo(55.dp)
            for (period in BillingPeriod.entries) {
                compose
                    .onNodeWithText(period.label)
                    .performScrollTo()
                    .performClick()
                    .assertIsSelected()
                    .assertHeightIsEqualTo(45.dp)
                BillingPeriod.entries
                    .filter { it != period }
                    .forEach {
                        compose.onNodeWithText(it.label).assertIsNotSelected()
                    }
            }
            compose.onNodeWithContentDescription("Дата списания").performScrollTo().performClick()
            compose.onNodeWithText("Выбрать").assertIsNotEnabled()

            compose.onNode(hasText("15", substring = true) and hasClickAction()).performClick()
            compose.onNodeWithText("Выбрать").assertIsEnabled().performClick()
            compose.onNodeWithText("Добавить подписку").performScrollTo().performClick()
            compose.runOnIdle {
                assertEquals("YouTube Премиум", result?.service)
                assertEquals("299,50", result?.amount)
                assertEquals(BillingPeriod.YEAR, result?.period)
                assertEquals(15, result?.date?.dayOfMonth)
            }
            compose.onNodeWithText("Отмена").performScrollTo().performClick()
            compose.runOnIdle { assertTrue(cancelled) }
        } finally {
            TimeZone.setDefault(previousZone)
        }
    }

    @Test
    fun filledPreviewAndMissingHandlerNeverReportSuccess() {
        compose.setContent {
            RecurraTheme {
                AddSubscriptionContent(
                    AddSubscriptionForm("YouTube Premium", "299", LocalDate.of(2026, 11, 4)),
                    {},
                    {},
                    {},
                    {},
                    null,
                    {},
                )
            }
        }
        compose.onNodeWithText("04.11.2026").assertExists()
        compose.onRoot().captureToImage().asAndroidBitmap().let { bitmap ->
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            File(context.cacheDir, "add-subscription-preview.png").outputStream().use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        }
        compose.onNodeWithText("Добавить подписку").performScrollTo().performClick()
        compose.onNodeWithText("Сохранение пока не подключено").assertExists()
    }

    @Test
    fun keyboardAllowsScrollingToActions() {
        compose.setContent { RecurraTheme { CreateSubscriptionScreen(onCancel = {}) } }
        compose.onNodeWithContentDescription("Сервис").performClick().performTextInput("Тест")
        compose.waitUntil(timeoutMillis = 5_000) {
            ViewCompat.getRootWindowInsets(compose.activity.window.decorView)
                ?.isVisible(WindowInsetsCompat.Type.ime()) == true
        }
        compose.onNodeWithText("Добавить подписку").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Отмена").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun textAndSelectionSurviveActivityRecreation() {
        val restoration = StateRestorationTester(compose)
        restoration.setContent { RecurraTheme { CreateSubscriptionScreen(onCancel = {}) } }
        compose.onNodeWithContentDescription("Сервис").performTextInput("Сервис Test")
        compose
            .onNodeWithContentDescription("Сумма списания")
            .performScrollTo()
            .performTextInput("123")
        compose.onNodeWithText("3 месяца").performScrollTo().performClick()
        restoration.emulateSavedInstanceStateRestore()
        compose.onNodeWithContentDescription("Сервис").assertTextContains("Сервис Test")
        compose.onNodeWithContentDescription("Сумма списания").assertTextContains("123")
        compose.onNodeWithText("3 месяца").assertIsSelected()
    }
}
