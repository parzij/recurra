package com.parzij.recurra.ui.components

import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.parzij.recurra.ui.home.HomeScreen
import com.parzij.recurra.ui.payments.PaymentsScreen
import com.parzij.recurra.ui.profile.ProfileScreen
import com.parzij.recurra.ui.theme.RecurraTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class RecurraAddFabTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    private fun fab() = compose.onNodeWithContentDescription("Добавить подписку")

    @Test
    fun allMainScreensHaveFabWithExactGeometry() {
        val screen = mutableStateOf(BottomBarDestination.HOME)
        compose.activityRule.scenario.onActivity { it.enableEdgeToEdge() }
        compose.setContent {
            RecurraTheme {
                when (screen.value) {
                    BottomBarDestination.HOME -> HomeScreen(userName = "Мария")
                    BottomBarDestination.PAYMENTS -> PaymentsScreen("Мария")
                    BottomBarDestination.PROFILE ->
                        ProfileScreen(
                            "Мария",
                            userEmail = "",
                            subscriptionsCount = 0,
                            onLogout = {},
                        )
                }
            }
        }
        val density =
            InstrumentationRegistry.getInstrumentation()
                .targetContext
                .resources
                .displayMetrics
                .density
        for (destination in BottomBarDestination.entries) {
            compose.runOnIdle { screen.value = destination }
            run {
                fab().assertWidthIsEqualTo(64.dp).assertHeightIsEqualTo(64.dp)
                val button = fab().fetchSemanticsNode().boundsInRoot
                val root = compose.onRoot().fetchSemanticsNode().boundsInRoot
                val bottom =
                    compose
                        .onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.SelectableGroup))
                        .fetchSemanticsNode()
                        .boundsInRoot
                assertEquals(15f * density, root.right - button.right, 1f)
                assertEquals(40f * density, bottom.top - button.bottom, 1f)
            }
        }
    }

    @Test
    fun allMainScreensForwardAddClickWithoutOpeningSheet() {
        val screen = mutableStateOf(BottomBarDestination.HOME)
        var clicks = 0
        val onAdd = {
            clicks++
            Unit
        }
        compose.setContent {
            RecurraTheme {
                when (screen.value) {
                    BottomBarDestination.HOME -> HomeScreen(userName = "Мария", onAddClick = onAdd)
                    BottomBarDestination.PAYMENTS -> PaymentsScreen("Мария", onAddClick = onAdd)
                    BottomBarDestination.PROFILE ->
                        ProfileScreen(
                            "Мария",
                            userEmail = "",
                            subscriptionsCount = 0,
                            onLogout = {},
                            onAddClick = onAdd,
                        )
                }
            }
        }
        for ((index, destination) in BottomBarDestination.entries.withIndex()) {
            compose.runOnIdle { screen.value = destination }
            fab().performClick()
            compose.runOnIdle { assertEquals(index + 1, clicks) }
            compose.onNodeWithText("Добавление подписки").assertDoesNotExist()
        }
    }

    @Test
    fun scrollingContentDoesNotMoveFabOrBottomBar() {
        compose.activityRule.scenario.onActivity { it.enableEdgeToEdge() }
        compose.setContent {
            RecurraTheme {
                MainScreenScaffold("Мария", BottomBarDestination.HOME, {}, {}, {}) {
                    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                        repeat(100) { Text("Test row $it") }
                    }
                }
            }
        }
        val before = fab().fetchSemanticsNode().boundsInRoot
        val panel =
            compose.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.SelectableGroup))
        val panelBefore = panel.fetchSemanticsNode().boundsInRoot
        compose.onNodeWithText("Test row 99").performScrollTo()
        assertEquals(before, fab().fetchSemanticsNode().boundsInRoot)
        assertEquals(panelBefore, panel.fetchSemanticsNode().boundsInRoot)
    }
}
