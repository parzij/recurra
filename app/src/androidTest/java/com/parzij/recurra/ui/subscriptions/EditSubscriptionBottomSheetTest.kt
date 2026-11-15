package com.parzij.recurra.ui.subscriptions

import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.test.platform.app.InstrumentationRegistry
import com.parzij.recurra.ui.home.*
import com.parzij.recurra.ui.theme.RecurraTheme
import java.io.File
import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class EditSubscriptionBottomSheetTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    private val title
        get() = compose.onNodeWithText("Редактировать подписку")

    private val service
        get() = compose.onNodeWithContentDescription("Сервис")

    private val amount
        get() = compose.onNodeWithContentDescription("Сумма списания")

    private fun close() {
        compose.onNodeWithContentDescription("Закрыть редактирование").performClick()
        compose.waitUntil(5_000) {
            compose.onAllNodesWithText("Редактировать подписку").fetchSemanticsNodes().isEmpty()
        }
    }

    private fun open(name: String) {
        compose.onNodeWithText(name).performScrollTo().performTouchInput { longClick() }
        title.assertExists()
    }

    private fun startHome(
        onSave: (SubscriptionUi) -> Unit = {},
        onPause: (String) -> Unit = {},
        onDelete: (String) -> Unit = {},
        onResume: (String) -> Unit = {},
    ) {
        compose.activityRule.scenario.onActivity { it.enableEdgeToEdge() }
        compose.setContent {
            RecurraTheme {
                HomeScreen(
                    subscriptions =
                        DemoHomeSubscriptions.map {
                            if (it.id == "spotify")
                                it.copy(billingPeriod = BillingPeriod.SIX_MONTHS)
                            else it
                        },
                    onSaveSubscription = onSave,
                    onPauseSubscription = onPause,
                    onDeleteSubscription = onDelete,
                    onResumeSubscription = onResume,
                )
            }
        }
    }

    @Test
    fun selectedIdDraftIsolationAndSaveAllFields() {
        val saved = mutableListOf<SubscriptionUi>()
        startHome(onSave = { saved += it })
        compose.onNodeWithText("Spotify").performScrollTo().performClick()
        title.assertDoesNotExist()
        open("Spotify")
        service.assertTextContains("Spotify")
        amount.assertTextContains("570")
        compose.onNodeWithText("20.11.2026").assertExists()
        compose.onNodeWithText("6 месяцев").assertIsSelected()
        service.performTextReplacement("Черновик")
        close()
        compose.onNodeWithText("Spotify").assertExists()
        open("Яндекс Плюс")
        service.assertTextContains("Яндекс Плюс")
        amount.assertTextContains("399")
        close()
        open("Spotify")
        service.assertTextContains("Spotify")
        service.performTextReplacement("Spotify Family")
        amount.performScrollTo().performTextReplacement("799,50")
        amount.performImeAction()
        compose.waitUntil(5_000) {
            ViewCompat.getRootWindowInsets(compose.activity.window.decorView)
                ?.isVisible(WindowInsetsCompat.Type.ime()) != true
        }
        compose.onNodeWithContentDescription("Дата списания").performScrollTo().performClick()
        compose.onNodeWithText("Выберите дату").assertExists()
        compose.onNode(hasText("15", substring = true) and hasClickAction()).performClick()
        compose.onNodeWithText("Выбрать").performClick()
        compose.onNodeWithText("год").performScrollTo().performClick()
        compose.onNodeWithText("Сохранить изменения").performScrollTo().performClick()
        compose.waitUntil(5_000) {
            compose.onAllNodesWithText("Редактировать подписку").fetchSemanticsNodes().isEmpty()
        }
        compose.onNodeWithText("Spotify Family").assertExists()
        compose.runOnIdle {
            assertEquals(1, saved.size)
            assertEquals("spotify", saved.single().id)
            assertEquals("799,50 ₽", saved.single().price)
            assertEquals(BillingPeriod.YEAR, saved.single().billingPeriod)
            assertEquals(LocalDate.of(2026, 11, 15), saved.single().billingDate)
            assertEquals("Spotify", DemoHomeSubscriptions.last().name)
        }
    }

    @Test
    fun pauseResumeAndDeleteOnlySelectedId() {
        val paused = mutableListOf<String>()
        val resumed = mutableListOf<String>()
        val deleted = mutableListOf<String>()
        startHome(
            onPause = { paused += it },
            onDelete = { deleted += it },
            onResume = { resumed += it },
        )
        open("Okko")
        compose.onNodeWithText("Приостановить").performScrollTo().performClick()
        compose.waitUntil(5_000) {
            compose.onAllNodesWithText("Редактировать подписку").fetchSemanticsNodes().isEmpty()
        }
        compose.onNodeWithText("Okko").assertExists()
        open("Okko")
        compose.onNodeWithText("Возобновить").performScrollTo().performClick()
        compose.waitUntil(5_000) {
            compose.onAllNodesWithText("Редактировать подписку").fetchSemanticsNodes().isEmpty()
        }
        open("Spotify")
        compose.onNodeWithText("Удалить").performScrollTo().performClick()
        title.assertDoesNotExist()
        compose.onNodeWithText("Spotify").assertDoesNotExist()
        compose.onNodeWithText("Okko").assertExists()
        compose.runOnIdle {
            assertEquals(listOf("okko"), paused)
            assertEquals(listOf("okko"), resumed)
            assertEquals(listOf("spotify"), deleted)
        }
    }

    @Test
    fun dismissByBackScrimAndSwipeKeepsOriginal() {
        startHome()
        open("Spotify")
        service.performTextReplacement("Черновик")

        amount.performScrollTo().performClick().performImeAction()
        compose.waitUntil(5_000) {
            ViewCompat.getRootWindowInsets(compose.activity.window.decorView)
                ?.isVisible(WindowInsetsCompat.Type.ime()) != true
        }

        InstrumentationRegistry.getInstrumentation()
            .uiAutomation
            .performGlobalAction(
                android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK
            )
        compose.waitUntil(5_000) {
            compose.onAllNodesWithText("Редактировать подписку").fetchSemanticsNodes().isEmpty()
        }
        open("Spotify")
        service.assertTextContains("Spotify")
        compose
            .onNode(hasContentDescription("Close sheet", substring = true))
            .performSemanticsAction(SemanticsActions.OnClick) { it() }
        compose.waitUntil(5_000) {
            compose.onAllNodesWithText("Редактировать подписку").fetchSemanticsNodes().isEmpty()
        }
        open("Spotify")
        compose.onNodeWithText("Редактировать подписку").performTouchInput {
            swipe(center, center + Offset(0f, 800f), durationMillis = 150)
        }
        compose.waitUntil(5_000) {
            compose.onAllNodesWithText("Редактировать подписку").fetchSemanticsNodes().isEmpty()
        }
        compose.onNodeWithText("Spotify").assertExists()
    }

    @Test
    fun validationRejectedSaveKeyboardAndPreview() {
        var calls = 0
        val subscription = DemoHomeSubscriptions.first()
        compose.setContent {
            RecurraTheme {
                EditSubscriptionBottomSheet(
                    subscription,
                    {},
                    {
                        calls++
                        false
                    },
                    {},
                    {},
                    {},
                )
            }
        }
        compose.waitForIdle()
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        android.os.SystemClock.sleep(250)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        File(context.cacheDir, "edit-subscription-sheet.png").outputStream().use {
            InstrumentationRegistry.getInstrumentation()
                .uiAutomation
                .takeScreenshot()
                .compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        service.performTextClearance()
        amount.performScrollTo().performTextReplacement("0")
        compose.onNodeWithText("Сохранить изменения").performScrollTo().performClick()
        compose.onNodeWithText("Введите название сервиса").assertExists()
        compose.onNodeWithText("Введите сумму больше нуля").assertExists()
        compose.runOnIdle { assertEquals(0, calls) }
        service.performScrollTo().performClick().performTextInput("Test")
        compose.waitUntil(5_000) {
            ViewCompat.getRootWindowInsets(compose.activity.window.decorView)
                ?.isVisible(WindowInsetsCompat.Type.ime()) == true
        }
        amount.performScrollTo().performTextReplacement("120")
        compose.onNodeWithText(" ₽", useUnmergedTree = true).assertExists()
        compose
            .onNodeWithText("Сохранить изменения")
            .performScrollTo()
            .assertIsDisplayed()
            .performClick()
        compose.onNodeWithText("Не удалось применить изменения").assertExists()
        title.assertExists()
        compose.runOnIdle { assertEquals(1, calls) }
    }
}
