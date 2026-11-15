package com.parzij.recurra.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parzij.recurra.ui.theme.RecurraBlue
import com.parzij.recurra.ui.theme.RecurraOrange
import com.parzij.recurra.ui.theme.RecurraRed
import com.parzij.recurra.ui.theme.RecurraTeal

enum class SubscriptionColor(val color: Color) {
    RED(RecurraRed),
    ORANGE(RecurraOrange),
    DARK_BLUE(RecurraBlue),
    GREEN(RecurraTeal);

    companion object {
        fun at(index: Int): SubscriptionColor = entries[index % 4]
    }
}

@Composable
internal fun SubscriptionAvatar(initial: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(36.dp).background(color, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            initial,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = Color.White,
        )
    }
}
