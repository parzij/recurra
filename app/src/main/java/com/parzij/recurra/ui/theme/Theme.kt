package com.parzij.recurra.ui.theme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

private val LightColorScheme =
    lightColorScheme(
        primary = RecurraPlum,
        secondary = RecurraTeal,
        background = RecurraBackground,
        onBackground = RecurraNavy,
        surface = RecurraBackground,
        onSurface = RecurraNavy,
    )

@Composable
fun RecurraTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
            content = content,
        )
    }
}
