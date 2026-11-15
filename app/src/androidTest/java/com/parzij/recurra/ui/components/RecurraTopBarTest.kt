package com.parzij.recurra.ui.components

import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.parzij.recurra.ui.home.HomeScreen
import com.parzij.recurra.ui.payments.PaymentsScreen
import com.parzij.recurra.ui.profile.ProfileScreen
import com.parzij.recurra.ui.theme.RecurraTheme
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class RecurraTopBarTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun variantsKeepLogoAndTitleGeometry() {
        val screen = mutableStateOf(TopBarScreen.Home)
        var profileClicks = 0
        compose.activityRule.scenario.onActivity { it.enableEdgeToEdge() }
        compose.setContent {
            RecurraTheme {
                when (screen.value) {
                    TopBarScreen.Home ->
                        HomeScreen(userName = "Вячеслав", onProfileClick = { profileClicks++ })
                    TopBarScreen.Calendar ->
                        PaymentsScreen(userName = "Вячеслав", onProfileClick = { profileClicks++ })
                    TopBarScreen.Profile ->
                        ProfileScreen(
                            userName = "Вячеслав",
                            userEmail = "",
                            subscriptionsCount = 0,
                            onLogout = {},
                            onProfileClick = { profileClicks++ },
                        )
                }
            }
        }
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val density = context.resources.displayMetrics.density
        var logoTop: Float? = null
        for ((variant, title) in
            listOf(
                TopBarScreen.Home to "Добрый день, Вячеслав",
                TopBarScreen.Calendar to "Ближайшие списания",
                TopBarScreen.Profile to "Профиль",
            )) {
            compose.runOnIdle { screen.value = variant }
            val logo =
                compose.onNodeWithContentDescription("Recurra").fetchSemanticsNode().boundsInRoot
            val heading =
                compose
                    .onNode(
                        hasText(title) and
                            SemanticsMatcher.keyNotDefined(SemanticsProperties.Selected)
                    )
                    .fetchSemanticsNode()
                    .boundsInRoot
            assertEquals(30f * density, logo.left, 1f)
            assertEquals(153f * density, logo.width, 1f)
            assertEquals(38f * density, logo.height, 1f)
            assertEquals(25f * density, heading.top - logo.bottom, 1f)
            assertEquals(logo.left, heading.left, 0.5f)
            if (logoTop == null) logoTop = logo.top else assertEquals(logoTop, logo.top, 0.5f)
            val buttons =
                compose.onAllNodes(
                    hasContentDescription("Профиль") and
                        SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)
                )
            buttons.assertCountEquals(1)
            buttons[0].assertWidthIsEqualTo(48.dp).assertHeightIsEqualTo(48.dp).performClick()
            compose.runOnIdle { assertEquals(variant.ordinal + 1, profileClicks) }
            compose.waitForIdle()
            File(context.cacheDir, "topbar-${variant.name}.png").outputStream().use {
                compose
                    .onRoot()
                    .captureToImage()
                    .asAndroidBitmap()
                    .compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        }
    }

    @Test
    fun dynamicNameAndConsumedInsets() {
        val name = mutableStateOf("  Мария  ")
        val parentConsumesInsets = mutableStateOf(false)
        compose.activityRule.scenario.onActivity { it.enableEdgeToEdge() }
        compose.setContent {
            RecurraTheme {
                Box(if (parentConsumesInsets.value) Modifier.safeDrawingPadding() else Modifier) {
                    RecurraTopBar(TopBarScreen.Home, userName = name.value)
                }
            }
        }
        compose.onNodeWithText("Добрый день, Мария").assertExists()
        val originalTop =
            compose.onNodeWithContentDescription("Recurra").fetchSemanticsNode().boundsInRoot.top
        compose.runOnIdle {
            parentConsumesInsets.value = true
            name.value = "Александр"
        }
        compose.onNodeWithText("Добрый день, Александр").assertExists()
        val consumedTop =
            compose.onNodeWithContentDescription("Recurra").fetchSemanticsNode().boundsInRoot.top
        assertEquals(originalTop, consumedTop, 0.5f)
        compose.runOnIdle { name.value = " " }
        compose.onNodeWithText("Добрый день").assertExists()
    }
}
