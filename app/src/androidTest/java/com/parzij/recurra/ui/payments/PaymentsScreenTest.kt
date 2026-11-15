package com.parzij.recurra.ui.payments

import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.parzij.recurra.ui.navigation.RecurraNavHost
import com.parzij.recurra.ui.theme.RecurraTheme
import java.io.File
import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class PaymentsScreenTest {
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

    @Test
    fun emptyPaymentsShowsZeroSummaryAndCenteredTwoLineMessage() {
        val payments = mutableStateOf(emptyList<PaymentUi>())
        compose.activityRule.scenario.onActivity { it.enableEdgeToEdge() }
        compose.setContent {
            RecurraTheme {
                PaymentsScreen(userName = "Вячеслав", payments = payments.value)
            }
        }
        compose.onNodeWithText("0 подписок").assertIsDisplayed()
        compose.onNodeWithText("0 ₽").assertIsDisplayed()
        compose.onNodeWithText("1 450 ₽").assertDoesNotExist()
        compose.onNodeWithText("YouTube Premium").assertDoesNotExist()
        val title = compose.onNodeWithText("Списаний пока нет").assertIsDisplayed()
        val description =
            compose
                .onNodeWithText("Добавьте первую подписку,\nи здесь появятся ближайшие платежи")
                .assertIsDisplayed()
        title.assertWidthIsEqualTo(288.dp)
        description.assertWidthIsEqualTo(288.dp)
        val root = compose.onRoot().fetchSemanticsNode().boundsInRoot
        assertEquals(root.center.x, title.fetchSemanticsNode().boundsInRoot.center.x, 1f)
        assertEquals(root.center.x, description.fetchSemanticsNode().boundsInRoot.center.x, 1f)
        val fab =
            compose
                .onNodeWithContentDescription("Добавить подписку")
                .fetchSemanticsNode()
                .boundsInRoot
        assertTrue(description.fetchSemanticsNode().boundsInRoot.bottom < fab.top)
        val layouts = mutableListOf<TextLayoutResult>()
        description.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertEquals(2, layouts.single().lineCount)
        assertFalse(layouts.single().hasVisualOverflow)
        saveScreenshot("payments-empty.png")
        compose.runOnIdle { payments.value = DemoPayments }
        compose.onNodeWithText("Списаний пока нет").assertDoesNotExist()
        compose.onNodeWithText("4 подписки").assertIsDisplayed()
        compose.onNodeWithText("Spotify").assertIsDisplayed()
        compose.runOnIdle { payments.value = emptyList() }
        compose.onNodeWithText("0 ₽").assertIsDisplayed()
        compose.onNodeWithText("Списаний пока нет").assertIsDisplayed()
    }

    @Test
    fun shortEmptyScreenCanScrollWholeMessageAboveFab() {
        compose.activityRule.scenario.onActivity { it.enableEdgeToEdge() }
        compose.setContent {
            RecurraTheme {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                    PaymentsScreen(
                        userName = "Вячеслав",
                        payments = emptyList(),
                        modifier = Modifier.width(320.dp).height(600.dp),
                    )
                }
            }
        }
        val list = compose.onNode(hasScrollToIndexAction())
        val panel =
            compose.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.SelectableGroup))
        assertEquals(
            panel.fetchSemanticsNode().boundsInRoot.top,
            list.fetchSemanticsNode().boundsInRoot.bottom,
            1f,
        )
        list.performScrollToIndex(2)
        compose.onNodeWithText("Списаний пока нет").assertIsDisplayed()
        val description =
            compose
                .onNodeWithText("Добавьте первую подписку,\nи здесь появятся ближайшие платежи")
                .assertIsDisplayed()
        val layouts = mutableListOf<TextLayoutResult>()
        description.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertFalse(layouts.single().hasVisualOverflow)
        val fab =
            compose
                .onNodeWithContentDescription("Добавить подписку")
                .fetchSemanticsNode()
                .boundsInRoot
        assertTrue(description.fetchSemanticsNode().boundsInRoot.bottom <= fab.top)
        saveScreenshot("payments-empty-short.png")
    }

    @Test
    fun demoShowsSummaryDatesColorsAndSelectedPaymentsTab() {
        val clicks = mutableListOf<String>()
        compose.activityRule.scenario.onActivity { it.enableEdgeToEdge() }
        compose.setContent {
            RecurraTheme {
                PaymentsScreen(userName = "Вячеслав", onPaymentClick = { clicks += it })
            }
        }
        compose.onNodeWithText("Ближайшие списания").assertIsDisplayed()
        compose.onNodeWithText("За текущий месяц").assertIsDisplayed()
        compose.onNodeWithText("4 подписки").assertIsDisplayed()
        compose.onNodeWithText("1 450 ₽").assertIsDisplayed()
        compose
            .onNode(
                hasText("Списания") and
                    SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab)
            )
            .assertIsSelected()
        compose.onAllNodesWithContentDescription("Добавить подписку").assertCountEquals(1)
        val colors = listOf(0xFFC63D4F, 0xFFF4901E, 0xFF104A65, 0xFF2F7D69)
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        val density = compose.activity.resources.displayMetrics.density
        val rows = DemoPayments.map {
            compose.onNodeWithText(it.name).fetchSemanticsNode().boundsInRoot
        }
        saveScreenshot("payments-populated.png")
        DemoPayments.forEachIndexed { index, payment ->
            compose.onNodeWithText(payment.name).assertIsDisplayed().assertHeightIsEqualTo(60.dp)
            compose
                .onNodeWithText(
                    "${formatPaymentDate(payment.date).day}\n${formatPaymentDate(payment.date).month}"
                )
                .assertIsDisplayed()
            compose.onNodeWithText(payment.price).assertIsDisplayed()
            val initial =
                compose
                    .onNodeWithText(payment.initial, useUnmergedTree = true)
                    .fetchSemanticsNode()
                    .boundsInRoot
            assertEquals(
                colors[index].toInt(),
                bitmap.getPixel(
                    (initial.center.x - 14 * density).toInt(),
                    initial.center.y.toInt(),
                ),
            )
            if (index > 0) assertEquals(5 * density, rows[index].top - rows[index - 1].bottom, 1f)
            compose.onNodeWithText(payment.name).performClick()
        }
        compose.runOnIdle { assertEquals(DemoPayments.map { it.id }, clicks) }
        compose
            .onAllNodesWithText("ноя", substring = true, useUnmergedTree = true)
            .assertCountEquals(4)
        compose.onAllNodesWithText("в месяц").assertCountEquals(0)
    }

    @Test
    fun twentyRowsScrollToLastAboveFabWithFullViewportAndFixedControls() {
        val payments =
            List(20) { index ->
                PaymentUi(
                    "id-$index",
                    LocalDate.of(2026, 11, index + 1),
                    "Сервис $index",
                    "999 ₽",
                    "X",
                )
            }
        compose.activityRule.scenario.onActivity { it.enableEdgeToEdge() }
        compose.setContent {
            RecurraTheme {
                PaymentsScreen(
                    userName = "Вячеслав",
                    payments = payments,
                    monthlyTotal = "99 999 999 ₽",
                )
            }
        }
        val layouts = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText("20 подписок").assertIsDisplayed()
        compose.onNodeWithText("99 999 999 ₽").performSemanticsAction(
            SemanticsActions.GetTextLayoutResult
        ) {
            it(layouts)
        }
        assertFalse(layouts.single().hasVisualOverflow)
        val list = compose.onNode(hasScrollToIndexAction())
        val panel =
            compose.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.SelectableGroup))
        val listBefore = list.fetchSemanticsNode().boundsInRoot
        val panelBefore = panel.fetchSemanticsNode().boundsInRoot
        val fabBefore =
            compose
                .onNodeWithContentDescription("Добавить подписку")
                .fetchSemanticsNode()
                .boundsInRoot
        assertEquals(panelBefore.top, listBefore.bottom, 1f)
        list.performScrollToIndex(21)
        compose.onNodeWithText("Сервис 19").assertIsDisplayed()
        val last = compose.onNodeWithText("Сервис 19").fetchSemanticsNode().boundsInRoot
        assertTrue(last.top >= listBefore.top)
        assertTrue(last.bottom <= fabBefore.top)
        assertEquals(
            fabBefore,
            compose
                .onNodeWithContentDescription("Добавить подписку")
                .fetchSemanticsNode()
                .boundsInRoot,
        )
        assertEquals(panelBefore, panel.fetchSemanticsNode().boundsInRoot)
        compose.onNodeWithContentDescription("Recurra").assertDoesNotExist()
        saveScreenshot("payments-long-list.png")
    }

    @Test
    fun dateFormattingUsesAllTwelveRussianMonthLabels() {
        val expected =
            listOf(
                "янв",
                "фев",
                "мар",
                "апр",
                "май",
                "июн",
                "июл",
                "авг",
                "сен",
                "окт",
                "ноя",
                "дек",
            )
        expected.forEachIndexed { index, month ->
            assertEquals(
                PaymentDateText("04", month),
                formatPaymentDate(LocalDate.of(2026, index + 1, 4)),
            )
        }
        assertEquals("29", formatPaymentDate(LocalDate.of(2024, 2, 29)).day)
    }

    @Test
    fun existingNavigationOpensPaymentsAndFullAddScreen() {
        compose.activityRule.scenario.onActivity { it.enableEdgeToEdge() }
        compose.setContent { RecurraTheme { RecurraNavHost(enableDebugUiDemo = true) } }
        compose.onNodeWithText("Войти").performScrollTo().performClick()
        compose.onNodeWithContentDescription("Почта").performTextInput("demo@example.com")
        compose
            .onNodeWithContentDescription("Пароль")
            .performScrollTo()
            .performTextInput("ui-only-pass")
        compose.onNodeWithContentDescription("Пароль").performImeAction()
        val paymentsTab =
            compose.onNode(
                hasText("Списания") and
                    SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab)
            )
        paymentsTab.performClick().assertIsSelected()
        compose.onNodeWithText("За текущий месяц").assertIsDisplayed()
        compose.onNodeWithText("04\nноя").assertIsDisplayed()
        compose.onNodeWithContentDescription("Добавить подписку").performClick()
        compose.onNodeWithText("Новая подписка").assertIsDisplayed()
        compose.onNodeWithText("Отмена").performScrollTo().performClick()
        paymentsTab.assertIsSelected()
        compose.onNodeWithText("За текущий месяц").assertIsDisplayed()
        compose
            .onNode(
                hasText("Подписки") and
                    SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab)
            )
            .performClick()
        compose.onNodeWithText("Расходы в месяц").assertIsDisplayed()
    }
}
