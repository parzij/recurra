package com.parzij.recurra

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.parzij.recurra.ui.auth.AuthFlow

@Composable
fun RecurraApp() {
    val context = LocalContext.current
    var demoNoticeShown by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (BuildConfig.DEBUG && !demoNoticeShown) {
            Toast.makeText(context, R.string.debug_ui_demo_notice, Toast.LENGTH_LONG).show()
            demoNoticeShown = true
        }
    }
    AuthFlow()
}
