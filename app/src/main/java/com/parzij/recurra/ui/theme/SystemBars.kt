package com.parzij.recurra.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

internal fun Window.configureRecurraSystemBars() {
    addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    WindowCompat.enableEdgeToEdge(this)
    if (Build.VERSION.SDK_INT >= 30) {
        insetsController?.setSystemBarsAppearance(
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
        )
    } else {
        WindowCompat.getInsetsController(this, decorView).isAppearanceLightStatusBars = true
    }
}

internal fun Window.keepComposeWindowInsets() {
    (findViewById<View>(android.R.id.content).parent as? View)?.fitsSystemWindows = false
}

@Composable
internal fun NavigationBarAppearance(darkBackground: Boolean) {
    val view = LocalView.current
    val window = view.context.findActivity()?.window ?: return
    DisposableEffect(window, view, darkBackground) {
        val previousLightAppearance =
            if (Build.VERSION.SDK_INT >= 30) {
                (window.insetsController?.systemBarsAppearance ?: 0) and
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS != 0
            } else {
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars
            }
        fun setLightAppearance(light: Boolean) {
            if (Build.VERSION.SDK_INT >= 30) {
                val mask = WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                window.insetsController?.setSystemBarsAppearance(if (light) mask else 0, mask)
            } else {
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars =
                    light
            }
        }
        val previousContrast = window.isNavigationBarContrastEnforced
        setLightAppearance(!darkBackground)

        window.isNavigationBarContrastEnforced = !darkBackground
        onDispose {
            setLightAppearance(previousLightAppearance)
            window.isNavigationBarContrastEnforced = previousContrast
        }
    }
}

private tailrec fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
