package com.parzij.recurra.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parzij.recurra.R
import com.parzij.recurra.ui.theme.RecurraCream
import com.parzij.recurra.ui.theme.RecurraTeal

@Composable
internal fun MoneyText(
    text: String,
    style: TextStyle,
    color: Color,
    maximumFontSize: Int,
    lineHeight: Int,
    fontWeight: FontWeight,
    modifier: Modifier = Modifier,
) {
    BasicText(
        text = text,
        modifier = modifier,
        style =
            style.copy(
                color = color,
                fontWeight = fontWeight,
                fontSize = maximumFontSize.sp,
                lineHeight = lineHeight.sp,
                letterSpacing = 0.sp,
            ),
        maxLines = 1,
        autoSize = TextAutoSize.StepBased(minFontSize = 8.sp, maxFontSize = maximumFontSize.sp),
    )
}

@Composable
internal fun PaymentSummaryCard(
    title: String,
    subtitle: String,
    amount: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(RecurraTeal, RoundedCornerShape(20.dp))
                .heightIn(min = 80.dp)
                .padding(horizontal = 18.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier.size(44.dp)
                    .background(Color.White.copy(alpha = 0.22f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painterResource(R.drawable.ic_calendar_outline_light),
                contentDescription = null,
                tint = RecurraCream,
                modifier = Modifier.size(30.dp),
            )
        }
        Column(Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = RecurraCream,
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = RecurraCream,
            )
        }
        MoneyText(
            text = amount,
            style = MaterialTheme.typography.titleLarge,
            color = RecurraCream,
            maximumFontSize = 24,
            lineHeight = 28,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.widthIn(max = 110.dp),
        )
    }
}
