package com.parzij.recurra.ui.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parzij.recurra.BuildConfig
import com.parzij.recurra.R
import com.parzij.recurra.ui.components.BottomBarDestination
import com.parzij.recurra.ui.components.MainScreenScaffold
import com.parzij.recurra.ui.components.MoneyText
import com.parzij.recurra.ui.components.PaymentSummaryCard
import com.parzij.recurra.ui.components.RecurraTopBar
import com.parzij.recurra.ui.components.SubscriptionAvatar
import com.parzij.recurra.ui.components.SubscriptionColor
import com.parzij.recurra.ui.components.TopBarScreen
import com.parzij.recurra.ui.subscriptions.EditSubscriptionBottomSheet
import com.parzij.recurra.ui.theme.RecurraPlum
import com.parzij.recurra.ui.theme.RecurraRed
import com.parzij.recurra.ui.theme.RecurraSand
import com.parzij.recurra.ui.theme.RecurraSlate
import com.parzij.recurra.ui.theme.RecurraTeal
import com.parzij.recurra.ui.theme.RecurraTheme
import com.parzij.recurra.ui.theme.RecurraWelcomeMint
import com.parzij.recurra.ui.theme.RecurraWelcomeRose
import com.parzij.recurra.ui.theme.RecurraWelcomeWallet

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    userName: String = "Вячеслав",
    onProfileClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onPaymentsClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    subscriptions: List<SubscriptionUi> =
        if (BuildConfig.DEBUG) DemoHomeSubscriptions else emptyList(),
    statistics: HomeStatisticsUi = DemoHomeStatistics,
    nextPayment: NextPaymentUi? = DemoNextPayment,
    onSubscriptionLongClick: (String) -> Unit = {},
    onPauseSubscription: (String) -> Unit = {},
    onSaveSubscription: (SubscriptionUi) -> Unit = {},
    onDeleteSubscription: (String) -> Unit = {},
    onResumeSubscription: (String) -> Unit = {},
    onSubscriptionsChange: ((List<SubscriptionUi>) -> Unit)? = null,
) {

    val localSubscriptions =
        if (onSubscriptionsChange == null) {
            rememberSaveable(subscriptions, stateSaver = SubscriptionUiListSaver) {
                mutableStateOf(subscriptions)
            }
        } else null
    val displayedSubscriptions = localSubscriptions?.value ?: subscriptions
    fun updateSubscriptions(updated: List<SubscriptionUi>) {
        if (localSubscriptions != null) localSubscriptions.value = updated
        else onSubscriptionsChange?.invoke(updated)
    }
    var selectedSubscriptionId by rememberSaveable { mutableStateOf<String?>(null) }
    val activeCount = displayedSubscriptions.count { it.status == SubscriptionStatus.ACTIVE }
    val activeNextPayment = nextPayment?.takeIf { payment ->
        displayedSubscriptions.any {
            it.id == payment.subscriptionId && it.status == SubscriptionStatus.ACTIVE
        }
    }
    BoxWithConstraints(modifier) {
        val scrollHeader = maxHeight < 500.dp
        MainScreenScaffold(
            destination = BottomBarDestination.HOME,
            modifier = Modifier.fillMaxSize(),
            showTopBar = !scrollHeader,
            userName = userName,
            onProfileClick = onProfileClick,
            onHomeClick = onHomeClick,
            onPaymentsClick = onPaymentsClick,
            onAddClick = onAddClick,
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 12.dp, bottom = 120.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (scrollHeader) {
                    item(key = "header") {
                        RecurraTopBar(
                            TopBarScreen.Home,
                            userName = userName,
                            onProfileClick = onProfileClick,
                        )
                    }
                }
                item(key = "statistics") {
                    MonthlyExpensesCard(
                        monthlyExpenses =
                            if (activeCount == 0) stringResource(R.string.home_zero_expenses)
                            else statistics.monthlyExpenses,
                        subscriptionCount = activeCount.toString(),
                        yearlyExpenses =
                            if (activeCount == 0) stringResource(R.string.home_zero_expenses)
                            else statistics.yearlyExpenses,
                        modifier = Modifier.padding(horizontal = 21.dp),
                    )
                }
                if (displayedSubscriptions.isEmpty()) {
                    item(key = "empty") {
                        EmptySubscriptionsState(Modifier.padding(top = 40.dp))
                    }
                } else {
                    if (activeNextPayment != null) {
                        item(key = "next-payment") {
                            PaymentSummaryCard(
                                title = stringResource(R.string.home_next_payment),
                                subtitle = activeNextPayment.description,
                                amount = activeNextPayment.price,
                                modifier =
                                    Modifier.padding(horizontal = 21.dp).padding(top = 15.dp),
                            )
                        }
                    }
                    item(key = "subscriptions-title") {
                        Text(
                            text = stringResource(R.string.home_my_subscriptions),
                            modifier =
                                Modifier.fillMaxWidth()
                                    .padding(horizontal = 30.dp)
                                    .padding(top = 15.dp, bottom = 20.dp),
                            style = MaterialTheme.typography.headlineSmall,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorResource(R.color.black),
                        )
                    }
                    itemsIndexed(
                        displayedSubscriptions,
                        key = { _, subscription -> "subscription:${subscription.id}" },
                    ) { index, subscription ->
                        SubscriptionListItem(
                            subscription = subscription,
                            color = SubscriptionColor.at(index),
                            onSubscriptionLongClick = { id ->
                                if (displayedSubscriptions.any { it.id == id }) {
                                    selectedSubscriptionId = id
                                    onSubscriptionLongClick(id)
                                }
                            },
                            modifier = Modifier.padding(horizontal = 21.dp),
                        )
                    }
                }
            }
        }
    }

    displayedSubscriptions
        .firstOrNull { it.id == selectedSubscriptionId }
        ?.let { selected ->
            EditSubscriptionBottomSheet(
                subscription = selected,
                onDismiss = { selectedSubscriptionId = null },
                onSave = { updated ->
                    onSaveSubscription(updated)
                    updateSubscriptions(
                        displayedSubscriptions.map {
                            if (it.id == updated.id) updated else it
                        }
                    )
                    true
                },
                onPause = { id ->
                    onPauseSubscription(id)
                    updateSubscriptions(
                        displayedSubscriptions.map {
                            if (it.id == id) it.copy(status = SubscriptionStatus.PAUSED) else it
                        }
                    )
                },
                onDelete = { id ->
                    onDeleteSubscription(id)
                    updateSubscriptions(displayedSubscriptions.filterNot { it.id == id })
                    selectedSubscriptionId = null
                },
                onResume = { id ->
                    onResumeSubscription(id)
                    updateSubscriptions(
                        displayedSubscriptions.map {
                            if (it.id == id) it.copy(status = SubscriptionStatus.ACTIVE) else it
                        }
                    )
                },
            )
        }
}

@Composable
private fun MonthlyExpensesCard(
    monthlyExpenses: String,
    subscriptionCount: String,
    yearlyExpenses: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .border(BorderStroke(2.dp, RecurraSand), RoundedCornerShape(22.dp))
                .padding(15.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        Row(
            modifier = Modifier.heightIn(min = 64.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier.size(64.dp)
                        .background(RecurraWelcomeWallet, RoundedCornerShape(15.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_empty_wallet),
                    contentDescription = null,
                    modifier = Modifier.size(44.dp),
                    tint = Color.Unspecified,
                )
            }
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.home_monthly_expenses),
                    style =
                        MaterialTheme.typography.bodyMedium.copy(
                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                        ),
                    color = RecurraSlate,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Medium,
                )
                MoneyText(
                    text = monthlyExpenses,
                    style =
                        MaterialTheme.typography.displaySmall.copy(
                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                        ),
                    color = RecurraPlum,
                    maximumFontSize = 44,
                    lineHeight = 46,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            StatisticsItem(
                icon = R.drawable.ic_bookmark,
                iconColor = RecurraTeal,
                iconBackground = RecurraWelcomeMint,
                value = subscriptionCount,
                label = stringResource(R.string.home_subscriptions_count),
                modifier = Modifier.weight(1f),
            )
            StatisticsItem(
                icon = R.drawable.ic_presentation_chart,
                iconColor = RecurraRed,
                iconBackground = RecurraWelcomeRose,
                value = yearlyExpenses,
                label = stringResource(R.string.home_per_year),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun StatisticsItem(
    @DrawableRes icon: Int,
    iconColor: Color,
    iconBackground: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .heightIn(min = 64.dp)
                .border(BorderStroke(2.dp, RecurraSand), RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(36.dp).background(iconBackground, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = iconColor,
            )
        }
        Column(Modifier.weight(1f)) {
            MoneyText(
                text = value,
                style =
                    MaterialTheme.typography.titleLarge.copy(
                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                    ),
                color = colorResource(R.color.black),
                maximumFontSize = 24,
                lineHeight = 26,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = label,
                style =
                    MaterialTheme.typography.bodyMedium.copy(
                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                    ),
                color = RecurraSlate,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun EmptySubscriptionsState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.widthIn(max = 280.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.img_home_subscriptions),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().height(190.dp),
            contentScale = ContentScale.Fit,
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.home_empty_title),
            style =
                MaterialTheme.typography.headlineSmall.copy(
                    platformStyle = PlatformTextStyle(includeFontPadding = false)
                ),
            color = colorResource(R.color.black),
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            lineHeight = 30.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.home_empty_description),
            style =
                MaterialTheme.typography.bodyMedium.copy(
                    platformStyle = PlatformTextStyle(includeFontPadding = false)
                ),
            color = RecurraSlate,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun SubscriptionListItem(
    subscription: SubscriptionUi,
    color: SubscriptionColor,
    modifier: Modifier = Modifier,
    onSubscriptionLongClick: (String) -> Unit = {},
) {
    val paused = subscription.status == SubscriptionStatus.PAUSED
    val textColor = if (paused) RecurraSlate else colorResource(R.color.black)
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = 54.dp)
                .combinedClickable(
                    onClick = {},
                    onLongClick = { onSubscriptionLongClick(subscription.id) },
                ),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SubscriptionAvatar(
            initial = subscription.initial,
            color = if (paused) RecurraSlate else color.color,
        )
        Text(
            subscription.name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
        )
        Column(Modifier.widthIn(max = 150.dp), horizontalAlignment = Alignment.End) {
            MoneyText(
                text = subscription.price,
                style = MaterialTheme.typography.titleLarge,
                color = textColor,
                maximumFontSize = 20,
                lineHeight = 24,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                subscription.billingPeriod.homeLabel,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium,
                color = RecurraSlate,
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun HomeScreenEmptyPreview() {
    RecurraTheme { HomeScreen(userName = "Вячеслав", subscriptions = emptyList()) }
}

@Preview(showSystemUi = true)
@Composable
fun HomeScreenPopulatedPreview() {
    RecurraTheme { HomeScreen(userName = "Вячеслав", subscriptions = DemoHomeSubscriptions) }
}

@Preview(showSystemUi = true)
@Composable
fun HomeScreenPausedPreview() {
    RecurraTheme {
        HomeScreen(
            userName = "Вячеслав",
            subscriptions = DemoPausedHomeSubscriptions,
            statistics = DemoPausedHomeStatistics,
            nextPayment = DemoPausedNextPayment,
        )
    }
}
