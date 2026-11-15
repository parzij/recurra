package com.parzij.recurra.ui.payments

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parzij.recurra.R
import com.parzij.recurra.ui.components.BottomBarDestination
import com.parzij.recurra.ui.components.MainScreenScaffold
import com.parzij.recurra.ui.components.MoneyText
import com.parzij.recurra.ui.components.PaymentSummaryCard
import com.parzij.recurra.ui.components.RecurraTopBar
import com.parzij.recurra.ui.components.SubscriptionAvatar
import com.parzij.recurra.ui.components.SubscriptionColor
import com.parzij.recurra.ui.components.TopBarScreen
import com.parzij.recurra.ui.theme.RecurraSand
import com.parzij.recurra.ui.theme.RecurraSlate
import com.parzij.recurra.ui.theme.RecurraTheme
import java.time.LocalDate
import java.time.Month

data class PaymentUi(
    val id: String,
    val date: LocalDate,
    val name: String,
    val price: String,
    val initial: String,
)

internal data class PaymentDateText(val day: String, val month: String)

internal fun formatPaymentDate(date: LocalDate): PaymentDateText =
    PaymentDateText(
        day = date.dayOfMonth.toString().padStart(2, '0'),
        month =
            when (date.month) {
                Month.JANUARY -> "янв"
                Month.FEBRUARY -> "фев"
                Month.MARCH -> "мар"
                Month.APRIL -> "апр"
                Month.MAY -> "май"
                Month.JUNE -> "июн"
                Month.JULY -> "июл"
                Month.AUGUST -> "авг"
                Month.SEPTEMBER -> "сен"
                Month.OCTOBER -> "окт"
                Month.NOVEMBER -> "ноя"
                Month.DECEMBER -> "дек"
            },
    )

val DemoPayments =
    listOf(
        PaymentUi("youtube", LocalDate.of(2026, 11, 4), "YouTube Premium", "299 ₽", "Y"),
        PaymentUi("yandex", LocalDate.of(2026, 11, 7), "Яндекс Плюс", "399 ₽", "Я"),
        PaymentUi("okko", LocalDate.of(2026, 11, 15), "Okko", "799 ₽", "O"),
        PaymentUi("spotify", LocalDate.of(2026, 11, 26), "Spotify", "570 ₽", "S"),
    )

private fun subscriptionCountLabel(count: Int): Int =
    when {
        count % 100 in 11..14 -> R.string.payments_subscription_count_many
        count % 10 == 1 -> R.string.payments_subscription_count_one
        count % 10 in 2..4 -> R.string.payments_subscription_count_few
        else -> R.string.payments_subscription_count_many
    }

@Composable
fun PaymentsScreen(
    userName: String,
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onPaymentsClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    payments: List<PaymentUi> = DemoPayments,
    monthlyTotal: String = "1 450 ₽",
    onPaymentClick: (String) -> Unit = {},
) {
    MainScreenScaffold(
        destination = BottomBarDestination.PAYMENTS,
        modifier = modifier,
        userName = userName,
        onProfileClick = onProfileClick,
        onHomeClick = onHomeClick,
        onPaymentsClick = onPaymentsClick,
        onAddClick = onAddClick,
        showTopBar = false,
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val empty = payments.isEmpty()
            val density = LocalDensity.current
            var headerHeightPx by remember { mutableIntStateOf(0) }
            var summaryHeightPx by remember { mutableIntStateOf(0) }
            var emptyHeightPx by remember { mutableIntStateOf(0) }
            val remainingHeight =
                (maxHeight -
                        with(density) {
                            (headerHeightPx + summaryHeightPx).toDp()
                        } -
                        16.dp)
                    .coerceAtLeast(0.dp)

            val needsExtraScroll =
                empty && remainingHeight < with(density) { emptyHeightPx.toDp() } + 176.dp
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding =
                    PaddingValues(bottom = if (!empty || needsExtraScroll) 120.dp else 16.dp),
            ) {
                item(key = "header") {
                    RecurraTopBar(
                        screen = TopBarScreen.Calendar,
                        modifier = Modifier.onSizeChanged { headerHeightPx = it.height },
                        userName = userName,
                        onProfileClick = onProfileClick,
                    )
                }
                item(key = "summary") {
                    PaymentSummaryCard(
                        title = stringResource(R.string.payments_current_month),
                        subtitle =
                            stringResource(
                                subscriptionCountLabel(payments.size),
                                payments.size,
                            ),
                        amount =
                            if (empty) stringResource(R.string.home_zero_expenses)
                            else monthlyTotal,
                        modifier =
                            Modifier.onSizeChanged { summaryHeightPx = it.height }
                                .padding(horizontal = 21.dp)
                                .padding(top = 20.dp, bottom = 15.dp),
                    )
                }
                if (empty) {
                    item(key = "empty") {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            EmptyPaymentsState(Modifier.onSizeChanged { emptyHeightPx = it.height })
                        }
                    }
                } else {
                    itemsIndexed(
                        payments,
                        key = { _, payment -> "payment:${payment.id}" },
                    ) { index, payment ->
                        PaymentListItem(
                            payment = payment,
                            color = SubscriptionColor.at(index),
                            onPaymentClick = onPaymentClick,
                            modifier =
                                Modifier.padding(horizontal = 21.dp)
                                    .padding(
                                        bottom = if (index < payments.lastIndex) 5.dp else 0.dp
                                    ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyPaymentsState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.widthIn(max = 288.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.img_calendar_wallet),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxWidth().height(192.dp),
        )
        Text(
            text = stringResource(R.string.payments_empty_title),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            style =
                MaterialTheme.typography.headlineSmall.copy(
                    platformStyle = PlatformTextStyle(includeFontPadding = false)
                ),
            fontSize = 24.sp,
            lineHeight = 30.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorResource(R.color.black),
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.payments_empty_description),
            modifier = Modifier.fillMaxWidth(),
            style =
                MaterialTheme.typography.bodyMedium.copy(
                    platformStyle = PlatformTextStyle(includeFontPadding = false)
                ),
            fontSize = 15.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium,
            color = RecurraSlate,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun PaymentListItem(
    payment: PaymentUi,
    color: SubscriptionColor,
    modifier: Modifier = Modifier,
    onPaymentClick: (String) -> Unit = {},
) {
    val dateText = formatPaymentDate(payment.date)
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(BorderStroke(2.dp, RecurraSand), RoundedCornerShape(16.dp))
                .clickable(role = Role.Button, onClick = { onPaymentClick(payment.id) })
                .heightIn(min = 60.dp)
                .padding(horizontal = 11.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier.widthIn(min = 48.dp)
                    .heightIn(min = 48.dp)
                    .background(RecurraSand, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text =
                    buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.sp,
                            )
                        ) {
                            append(dateText.day)
                        }
                        append('\n')
                        withStyle(SpanStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal)) {
                            append(dateText.month)
                        }
                    },
                style =
                    MaterialTheme.typography.bodyMedium.copy(
                        platformStyle = PlatformTextStyle(includeFontPadding = false),
                        lineHeightStyle =
                            LineHeightStyle(
                                LineHeightStyle.Alignment.Center,
                                LineHeightStyle.Trim.Both,
                            ),
                    ),
                lineHeight = 10.sp,
                textAlign = TextAlign.Center,
                color = colorResource(R.color.black),
            )
        }
        SubscriptionAvatar(initial = payment.initial, color = color.color)
        Text(
            payment.name,
            modifier = Modifier.weight(1f).padding(start = 6.dp),
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorResource(R.color.black),
        )
        MoneyText(
            text = payment.price,
            style = MaterialTheme.typography.titleMedium,
            color = colorResource(R.color.black),
            maximumFontSize = 20,
            lineHeight = 22,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.widthIn(max = 105.dp),
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun PaymentsScreenPopulatedPreview() {
    RecurraTheme { PaymentsScreen(userName = "Вячеслав", payments = DemoPayments) }
}

@Preview(showSystemUi = true)
@Composable
fun PaymentsScreenEmptyPreview() {
    RecurraTheme { PaymentsScreen(userName = "Вячеслав", payments = emptyList()) }
}
