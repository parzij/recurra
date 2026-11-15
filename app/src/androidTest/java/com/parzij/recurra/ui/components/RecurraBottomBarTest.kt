package com.parzij.recurra.ui.components

import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.test.platform.app.InstrumentationRegistry
import com.parzij.recurra.ui.home.HomeScreen
import com.parzij.recurra.ui.payments.PaymentsScreen
import com.parzij.recurra.ui.profile.ProfileScreen
import com.parzij.recurra.ui.theme.RecurraTheme
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class RecurraBottomBarTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    private fun tab(label: String) =
        compose.onNode(
            hasText(label) and SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab)
        )

    private fun panel() =
        compose.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.SelectableGroup))

    @Test
    fun screensShowSelectedTabsAndForwardNavigation() {
        val destination = mutableStateOf(BottomBarDestination.HOME)
        var transitions = 0
        val home = {
            transitions++
            destination.value = BottomBarDestination.HOME
        }
        val payments = {
            transitions++
            destination.value = BottomBarDestination.PAYMENTS
        }
        val profile = {
            transitions++
            destination.value = BottomBarDestination.PROFILE
        }
        compose.activityRule.scenario.onActivity { it.enableEdgeToEdge() }
        compose.setContent {
            RecurraTheme {
                when (destination.value) {
                    BottomBarDestination.HOME ->
                        HomeScreen(
                            userName = "Вячеслав",
                            onHomeClick = home,
                            onPaymentsClick = payments,
                            onProfileClick = profile,
                        )
                    BottomBarDestination.PAYMENTS ->
                        PaymentsScreen(
                            "Вячеслав",
                            onHomeClick = home,
                            onPaymentsClick = payments,
                            onProfileClick = profile,
                        )
                    BottomBarDestination.PROFILE ->
                        ProfileScreen(
                            "Вячеслав",
                            userEmail = "",
                            subscriptionsCount = 0,
                            onLogout = {},
                            onHomeClick = home,
                            onPaymentsClick = payments,
                            onProfileClick = profile,
                        )
                }
            }
        }
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val density = context.resources.displayMetrics.density
        for ((index, label) in listOf("Подписки", "Списания", "Профиль").withIndex()) {
            if (index > 0) tab(label).performClick()
            tab(label).assertIsSelected().assertWidthIsEqualTo(110.dp).assertHeightIsEqualTo(65.dp)
            compose.onAllNodes(isSelected()).assertCountEquals(1)
            panel().assertHeightIsEqualTo(85.dp)
            val rootBounds = compose.onRoot().fetchSemanticsNode().boundsInRoot
            val panelBounds = panel().fetchSemanticsNode().boundsInRoot
            val activeBounds = tab(label).fetchSemanticsNode().boundsInRoot
            assertEquals(rootBounds.width, panelBounds.width, 0.5f)
            assertEquals(panelBounds.center.y, activeBounds.center.y, 0.5f)
            val navInsets =
                ViewCompat.getRootWindowInsets(compose.activity.window.decorView)!!.getInsets(
                        WindowInsetsCompat.Type.navigationBars()
                    )
                    .bottom
            assertEquals(rootBounds.bottom - navInsets, panelBounds.bottom, 1f)
            for (inactive in listOf("Подписки", "Списания", "Профиль").filter { it != label }) {
                tab(inactive).assertIsNotSelected().assertHeightIsAtLeast(48.dp)
                org.junit.Assert.assertTrue(
                    tab(inactive).fetchSemanticsNode().boundsInRoot.width < 110f * density
                )
            }

            tab(label).performClick()
            compose.runOnIdle { assertEquals(index, transitions) }
            compose.waitForIdle()
            File(context.cacheDir, "bottom-${destination.value.name}.png").outputStream().use {
                compose
                    .onRoot()
                    .captureToImage()
                    .asAndroidBitmap()
                    .compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        }
        tab("Подписки").performClick()
        tab("Подписки").assertIsSelected()
        compose.runOnIdle { assertEquals(3, transitions) }
    }

    @Test
    fun navigationInsetsAreNotAppliedTwice() {
        val parentConsumesInsets = mutableStateOf(false)
        compose.activityRule.scenario.onActivity { it.enableEdgeToEdge() }
        compose.setContent {
            RecurraTheme {
                Box(
                    Modifier.fillMaxSize()
                        .then(
                            if (parentConsumesInsets.value) Modifier.navigationBarsPadding()
                            else Modifier
                        ),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    RecurraBottomBar(BottomBarDestination.HOME, {}, {}, {})
                }
            }
        }
        val originalBottom = panel().fetchSemanticsNode().boundsInRoot.bottom
        compose.runOnIdle { parentConsumesInsets.value = true }
        assertEquals(originalBottom, panel().fetchSemanticsNode().boundsInRoot.bottom, 0.5f)
        panel().assertHeightIsEqualTo(85.dp)
    }
}
