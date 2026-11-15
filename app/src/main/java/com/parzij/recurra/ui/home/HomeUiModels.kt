package com.parzij.recurra.ui.home

import androidx.compose.runtime.saveable.listSaver
import com.parzij.recurra.ui.subscriptions.BillingPeriod
import java.time.LocalDate

data class SubscriptionUi(
    val id: String,
    val name: String,
    val price: String,
    val initial: String,
    val billingPeriod: BillingPeriod = BillingPeriod.MONTH,
    val status: SubscriptionStatus = SubscriptionStatus.ACTIVE,
    val billingDate: LocalDate? = null,
)

enum class SubscriptionStatus {
    ACTIVE,
    PAUSED,
}

data class HomeStatisticsUi(val monthlyExpenses: String, val yearlyExpenses: String)

data class NextPaymentUi(val description: String, val price: String, val subscriptionId: String)

internal val SubscriptionUiListSaver =
    listSaver<List<SubscriptionUi>, String>(
        save = { subscriptions ->
            subscriptions.flatMap { subscription ->
                listOf(
                    subscription.id,
                    subscription.name,
                    subscription.price,
                    subscription.initial,
                    subscription.billingPeriod.name,
                    subscription.status.name,
                    subscription.billingDate?.toEpochDay()?.toString().orEmpty(),
                )
            }
        },
        restore = { values ->
            values.chunked(7).map { value ->
                SubscriptionUi(
                    value[0],
                    value[1],
                    value[2],
                    value[3],
                    billingPeriod = BillingPeriod.valueOf(value[4]),
                    status = SubscriptionStatus.valueOf(value[5]),
                    billingDate = value[6].toLongOrNull()?.let(LocalDate::ofEpochDay),
                )
            }
        },
    )
