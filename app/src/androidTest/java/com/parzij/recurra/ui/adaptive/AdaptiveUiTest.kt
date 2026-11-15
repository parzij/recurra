package com.parzij.recurra.ui.adaptive

import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.parzij.recurra.ui.home.HomeScreen
import com.parzij.recurra.ui.payments.PaymentsScreen
import com.parzij.recurra.ui.profile.ProfileScreen
import com.parzij.recurra.ui.theme.RecurraTheme
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
class AdaptiveUiTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun mainTabsFitCompactReferenceTabletLandscapeAndScaledFonts() {
        val size = mutableStateOf(DpSize(412.dp, 917.dp))
        val scale = mutableStateOf(1f)
        val screen = mutableStateOf(0)
        compose.setContent {
            DeviceConfigurationOverride(
                DeviceConfigurationOverride.ForcedSize(size.value) then
                    DeviceConfigurationOverride.FontScale(scale.value)
            ) {
                RecurraTheme {
                    when (screen.value) {
                        0 -> HomeScreen(userName = "Вячеслав")
                        1 -> PaymentsScreen(userName = "Вячеслав", payments = emptyList())
                        else ->
                            ProfileScreen(
                                userName = "Вячеслав",
                                userEmail = "test@example.com",
                                subscriptionsCount = 4,
                                onLogout = {},
                            )
                    }
                }
            }
        }
        for (dimensions in
            listOf(
                DpSize(320.dp, 640.dp),
                DpSize(412.dp, 917.dp),
                DpSize(840.dp, 1180.dp),
                DpSize(917.dp, 412.dp),
            )) {
            for (fontScale in listOf(1f, 1.6f, 2f)) {
                for (destination in 0..2) {
                    compose.runOnIdle {
                        size.value = dimensions
                        scale.value = fontScale
                        screen.value = destination
                    }
                    val label = listOf("Подписки", "Списания", "Профиль")[destination]
                    val layouts = mutableListOf<TextLayoutResult>()
                    compose
                        .onNode(
                            hasText(label) and hasAnyAncestor(isSelectable()),
                            useUnmergedTree = true,
                        )
                        .performSemanticsAction(SemanticsActions.GetTextLayoutResult) {
                            it(layouts)
                        }
                    val layout = layouts.single()
                    val linesFit =
                        (0 until layout.lineCount).all {
                            layout.getLineLeft(it) >= -1f &&
                                layout.getLineRight(it) <= layout.size.width + 1f &&
                                layout.getLineBottom(it) <= layout.size.height + 1f
                        }
                    assertTrue("$dimensions font=$fontScale $label size=${layout.size}", linesFit)
                    compose.onNodeWithContentDescription("Добавить подписку").assertIsDisplayed()
                    if (destination == 1) {
                        compose.onNode(hasScrollToIndexAction()).performScrollToIndex(2)
                        compose
                            .onNodeWithText("Списаний пока нет")
                            .performScrollTo()
                            .assertIsDisplayed()
                    }
                    if (destination == 2)
                        compose
                            .onNodeWithText("Выйти из аккаунта")
                            .performScrollTo()
                            .assertIsDisplayed()
                }
            }
        }
    }
}
