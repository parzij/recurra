package com.parzij.recurra.ui.profile

import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.test.platform.app.InstrumentationRegistry
import com.parzij.recurra.ui.theme.RecurraTheme
import java.io.File
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class ProfileScreenTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun profileUsesSuppliedIdentityAndCountWithInformationalCard() {
        var logoutCalls = 0
        compose.activityRule.scenario.onActivity { it.enableEdgeToEdge() }
        compose.setContent {
            RecurraTheme {
                ProfileScreen("Вячеслав", "parzij@example.com", 4, onLogout = { logoutCalls++ })
            }
        }
        compose
            .onNode(
                hasText("Профиль") and
                    SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab)
            )
            .assertIsSelected()
        val card = compose.onNodeWithText("Всего подписок")
        card.assertHeightIsEqualTo(55.dp)
        card.assert(SemanticsMatcher.keyNotDefined(SemanticsActions.OnClick))
        compose.onNodeWithText("4").assertExists()
        for ((text, size, weight) in
            listOf(
                Triple("Вячеслав", 22.sp, FontWeight.Bold),
                Triple("parzij@example.com", 16.sp, FontWeight.Medium),
            )) {
            val layouts = mutableListOf<TextLayoutResult>()
            compose.onNodeWithText(text).assertIsDisplayed().performSemanticsAction(
                SemanticsActions.GetTextLayoutResult
            ) {
                it(layouts)
            }
            assertEquals(size, layouts.single().layoutInput.style.fontSize)
            assertEquals(weight, layouts.single().layoutInput.style.fontWeight)
        }
        val button = compose.onNodeWithText("Выйти из аккаунта")
        button.assertHeightIsEqualTo(55.dp)
        val density = compose.activity.resources.displayMetrics.density
        val cardBounds = card.fetchSemanticsNode().boundsInRoot
        val buttonBounds = button.fetchSemanticsNode().boundsInRoot
        val rootBounds = compose.onRoot().fetchSemanticsNode().boundsInRoot
        assertEquals(rootBounds.width - 60 * density, cardBounds.width, 1f)
        assertEquals(cardBounds.width, buttonBounds.width, 1f)
        assertEquals(30 * density, cardBounds.left, 1f)
        assertEquals(13 * density, buttonBounds.top - cardBounds.bottom, 1f)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        File(context.cacheDir, "profile-populated.png").outputStream().use {
            compose
                .onRoot()
                .captureToImage()
                .asAndroidBitmap()
                .compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        button.performClick()
        compose.runOnIdle { assertEquals(1, logoutCalls) }
    }

    @Test
    fun longEmailWrapsWithinNarrowProfileAndZeroCountIsShown() {
        val email = "a.very.long.email.address.for.the.current.user@example-with-a-long-domain.com"
        compose.activityRule.scenario.onActivity { it.enableEdgeToEdge() }
        compose.setContent {
            RecurraTheme {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                    ProfileScreen(
                        "Александр",
                        email,
                        0,
                        onLogout = {},
                        modifier = Modifier.width(320.dp),
                    )
                }
            }
        }
        val layouts = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText(email).assertIsDisplayed().performSemanticsAction(
            SemanticsActions.GetTextLayoutResult
        ) {
            it(layouts)
        }
        assertTrue(layouts.single().lineCount > 1)
        assertFalse(layouts.single().hasVisualOverflow)
        compose.onNodeWithText("0").assertExists()
        compose.onNodeWithText("Выйти из аккаунта").performScrollTo().assertIsDisplayed()
    }
}
