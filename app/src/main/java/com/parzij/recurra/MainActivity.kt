package com.parzij.recurra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.parzij.recurra.ui.theme.RecurraTheme
import com.parzij.recurra.ui.theme.configureRecurraSystemBars
import com.parzij.recurra.ui.theme.keepComposeWindowInsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.configureRecurraSystemBars()
        setContent {
            RecurraTheme { RecurraApp() }
        }
        window.keepComposeWindowInsets()
    }
}
