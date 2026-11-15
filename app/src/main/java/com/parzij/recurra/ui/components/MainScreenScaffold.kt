package com.parzij.recurra.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.parzij.recurra.ui.theme.RecurraBackground

@Composable
internal fun MainScreenScaffold(
    userName: String,
    destination: BottomBarDestination,
    onHomeClick: () -> Unit,
    onPaymentsClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit = {},
    showTopBar: Boolean = true,
    content: @Composable BoxScope.() -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        containerColor = RecurraBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            if (showTopBar) {
                RecurraTopBar(
                    screen =
                        when (destination) {
                            BottomBarDestination.HOME -> TopBarScreen.Home
                            BottomBarDestination.PAYMENTS -> TopBarScreen.Calendar
                            BottomBarDestination.PROFILE -> TopBarScreen.Profile
                        },
                    userName = userName,
                    onProfileClick = onProfileClick,
                )
            }
        },
        bottomBar = {
            RecurraBottomBar(destination, onHomeClick, onPaymentsClick, onProfileClick)
        },
    ) { contentPadding ->
        Box(
            modifier =
                Modifier.fillMaxSize().padding(contentPadding).consumeWindowInsets(contentPadding)
        ) {
            content()

            RecurraAddFab(
                onAddClick = onAddClick,
                modifier = Modifier.align(Alignment.BottomEnd).padding(end = 15.dp, bottom = 40.dp),
            )
        }
    }
}
