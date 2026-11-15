package com.parzij.recurra.ui.home

import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.parzij.recurra.ui.theme.RecurraTheme
import java.io.File
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    private fun saveScreenshot(name: String) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        File(context.cacheDir, name).outputStream().use {
            compose
                .onRoot()
                .captureToImage()
                .asAndroidBitmap()
                .compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }

    private fun assertIconColor(initial: String, expected: Int) {
        val bounds =
            compose
                .onNodeWithText(initial, useUnmergedTree = true)
                .fetchSemanticsNode()
                .boundsInRoot
        val density = compose.activity.resources.displayMetrics.density
        val image = compose.onRoot().captureToImage().asAndroidBitmap()
        assertEquals(
            expected,
            image.getPixel((bounds.center.x - 14 * density).toInt(), bounds.center.y.toInt()),
        )
    }

    @Test
    fun emptyStateKeepsZeroStatisticsAndControls() {
        compose.activityRule.scenario.onActivity { it.enableEdgeToEdge() }
        compose.setContent { RecurraTheme { HomeScreen(subscriptions = emptyList()) } }
        compose.onNodeWithText("Пока здесь пусто").assertIsDisplayed()
        compose.onAllNodesWithText("0 ₽").assertCountEquals(2)
        compose.onNodeWithText("0").assertExists()
        compose.onNodeWithText("Мои подписки").assertDoesNotExist()
        compose.onNodeWithContentDescription("Добавить подписку").assertIsDisplayed()
        saveScreenshot("home-empty.png")
    }

    @Test
    fun populatedColorsFollowPositionAfterInsertionAndRemoval() {
        val subscriptions = mutableStateOf(DemoHomeSubscriptions)
        compose.activityRule.scenario.onActivity { it.enableEdgeToEdge() }
        compose.setContent { RecurraTheme { HomeScreen(subscriptions = subscriptions.value) } }
        compose.onNodeWithText("2 477 ₽").assertIsDisplayed()
        compose.onNodeWithText("29 724 ₽").assertIsDisplayed()
        compose.onNodeWithText("4").assertExists()
        compose.onNodeWithText("Spotify").assertIsDisplayed()
        compose.onNodeWithText("Пока здесь пусто").assertDoesNotExist()
        assertIconColor("Y", 0xFFC63D4F.toInt())
        assertIconColor("Я", 0xFFF4901E.toInt())
        assertIconColor("O", 0xFF104A65.toInt())
        saveScreenshot("home-populated.png")
        compose.onNodeWithText("Spotify").performScrollTo().assertIsDisplayed()
        assertIconColor("S", 0xFF2F7D69.toInt())
        compose.runOnIdle {
            subscriptions.value += SubscriptionUi("fifth", "Пятая подписка", "100 ₽", "Z")
        }
        compose.onNode(hasScrollToIndexAction()).performScrollToIndex(7)
        compose.onNodeWithText("Пятая подписка").assertIsDisplayed()
        assertIconColor("Z", 0xFFC63D4F.toInt())
        compose.runOnIdle { subscriptions.value = subscriptions.value.drop(1) }
        compose.onNode(hasScrollToIndexAction()).performScrollToIndex(3)
        assertIconColor("Я", 0xFFC63D4F.toInt())
    }

    @Test
    fun longAmountsFitAndFiftyRowsScrollAboveFixedControls() {
        val rows =
            List(50) { index ->
                SubscriptionUi("id-$index", "Подписка $index", "9 999 999 ₽", "X")
            }
        var addClicks = 0
        compose.activityRule.scenario.onActivity { it.enableEdgeToEdge() }
        compose.setContent {
            RecurraTheme {
                HomeScreen(
                    modifier = Modifier.width(320.dp),
                    subscriptions = rows,
                    statistics = HomeStatisticsUi("99 999 999 ₽", "1 199 999 988 ₽"),
                    nextPayment = NextPaymentUi("Подписка 0 · 04 ноя", "9 999 999 ₽", "id-0"),
                    onAddClick = { addClicks++ },
                )
            }
        }
        for (amount in listOf("99 999 999 ₽", "1 199 999 988 ₽")) {
            val layouts = mutableListOf<TextLayoutResult>()
            compose.onNodeWithText(amount).performSemanticsAction(
                SemanticsActions.GetTextLayoutResult
            ) {
                it(layouts)
            }
            assertFalse("Amount must fit: $amount", layouts.single().hasVisualOverflow)
            assertEquals(1, layouts.single().lineCount)
        }
        val list = compose.onNode(hasScrollToIndexAction())
        val panel =
            compose.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.SelectableGroup))
        val listBounds = list.fetchSemanticsNode().boundsInRoot
        val panelBounds = panel.fetchSemanticsNode().boundsInRoot
        assertEquals(panelBounds.top, listBounds.bottom, 1f)
        val originalFab =
            compose
                .onNodeWithContentDescription("Добавить подписку")
                .fetchSemanticsNode()
                .boundsInRoot
        compose.onNode(hasScrollToIndexAction()).performScrollToIndex(52)
        compose.onNodeWithText("Подписка 49").performScrollTo().assertIsDisplayed()
        val lastRow = compose.onNodeWithText("Подписка 49").fetchSemanticsNode().boundsInRoot
        val bottomTab = compose.onNodeWithText("Подписки").fetchSemanticsNode().boundsInRoot
        assertTrue(lastRow.bottom < bottomTab.top)
        val fab =
            compose
                .onNodeWithContentDescription("Добавить подписку")
                .fetchSemanticsNode()
                .boundsInRoot
        assertEquals(originalFab, fab)
        assertEquals(panelBounds, panel.fetchSemanticsNode().boundsInRoot)
        assertTrue(lastRow.bottom <= listBounds.bottom)
        compose.onNodeWithContentDescription("Добавить подписку").assertIsDisplayed().performClick()
        compose.runOnIdle { assertEquals(1, addClicks) }
        saveScreenshot("home-long-list.png")
    }

    @Test
    fun pausedStyleKeepsPositionsAndLongClickOpensEditorWithId() {
        val subscriptions = mutableStateOf(DemoPausedHomeSubscriptions)
        val longClicks = mutableListOf<String>()
        val pauseRequests = mutableListOf<String>()
        compose.activityRule.scenario.onActivity { it.enableEdgeToEdge() }
        compose.setContent {
            RecurraTheme {
                HomeScreen(
                    subscriptions = subscriptions.value,
                    statistics = DemoPausedHomeStatistics,
                    nextPayment = DemoPausedNextPayment,
                    onSubscriptionLongClick = { longClicks += it },
                    onPauseSubscription = { pauseRequests += it },
                )
            }
        }
        compose.onNodeWithText("3").assertExists()
        compose.onNodeWithText("1 768 ₽").assertIsDisplayed()
        compose.onNodeWithText("21 216 ₽").assertIsDisplayed()
        compose.onNodeWithText("Яндекс Плюс · 04 ноя").assertIsDisplayed()
        assertIconColor("Y", 0xFF5D6170.toInt())
        assertIconColor("Я", 0xFFF4901E.toInt())
        assertIconColor("O", 0xFF104A65.toInt())
        for (text in listOf("YouTube Premium", "299 ₽")) {
            val layouts = mutableListOf<TextLayoutResult>()
            compose.onNodeWithText(text, useUnmergedTree = true).performSemanticsAction(
                SemanticsActions.GetTextLayoutResult
            ) {
                it(layouts)
            }
            assertEquals(
                com.parzij.recurra.ui.theme.RecurraSlate,
                layouts.single().layoutInput.style.color,
            )
        }
        saveScreenshot("home-paused.png")
        compose.onNodeWithText("YouTube Premium").performTouchInput { longClick() }
        compose.onNodeWithText("Редактировать подписку").assertExists()
        compose.onNodeWithContentDescription("Сервис").assertTextContains("YouTube Premium")
        compose.onNodeWithText("Возобновить").assertExists()
        compose.onNodeWithContentDescription("Закрыть редактирование").performClick()
        compose.waitForIdle()
        compose.onNodeWithText("Яндекс Плюс").performTouchInput { longClick() }
        compose.onNodeWithContentDescription("Сервис").assertTextContains("Яндекс Плюс")
        compose.onNodeWithContentDescription("Закрыть редактирование").performClick()
        compose.runOnIdle {
            assertEquals(listOf("youtube", "yandex"), longClicks)
            assertTrue(pauseRequests.isEmpty())
            assertEquals(SubscriptionStatus.PAUSED, subscriptions.value.first().status)
        }
        compose.onAllNodes(isDialog()).assertCountEquals(0)
        compose.onNodeWithText("Spotify").performScrollTo()
        assertIconColor("S", 0xFF2F7D69.toInt())
        compose.runOnIdle {
            subscriptions.value += SubscriptionUi("fifth", "Пятая подписка", "100 ₽", "Z")
        }
        compose.onNode(hasScrollToIndexAction()).performScrollToIndex(7)
        assertIconColor("Z", 0xFFC63D4F.toInt())
    }

    @Test
    fun allPausedKeepsListAndHidesIneligibleNextPayment() {
        compose.activityRule.scenario.onActivity { it.enableEdgeToEdge() }
        compose.setContent {
            RecurraTheme {
                HomeScreen(
                    subscriptions =
                        DemoHomeSubscriptions.map { it.copy(status = SubscriptionStatus.PAUSED) },
                    nextPayment = DemoNextPayment,
                )
            }
        }
        compose.onNodeWithText("0").assertExists()
        compose.onAllNodesWithText("0 ₽").assertCountEquals(2)
        compose.onNodeWithText("Пока здесь пусто").assertDoesNotExist()
        compose.onNodeWithText("Ближайшее списание").assertDoesNotExist()
        compose.onNodeWithText("YouTube Premium · 04 ноя").assertDoesNotExist()
        compose.onNodeWithText("YouTube Premium").assertIsDisplayed()
        compose.onNodeWithText("Spotify").assertIsDisplayed()
        assertIconColor("Y", 0xFF5D6170.toInt())
    }
}
