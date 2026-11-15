package com.parzij.recurra.ui.home

import java.time.LocalDate

val DemoHomeSubscriptions =
    listOf(
        SubscriptionUi(
            "youtube",
            "YouTube Premium",
            "299 ₽",
            "Y",
            billingDate = LocalDate.of(2026, 11, 4),
        ),
        SubscriptionUi(
            "yandex",
            "Яндекс Плюс",
            "399 ₽",
            "Я",
            billingDate = LocalDate.of(2026, 11, 8),
        ),
        SubscriptionUi("okko", "Okko", "799 ₽", "O", billingDate = LocalDate.of(2026, 11, 15)),
        SubscriptionUi(
            "spotify",
            "Spotify",
            "570 ₽",
            "S",
            billingDate = LocalDate.of(2026, 11, 20),
        ),
    )

val DemoHomeStatistics = HomeStatisticsUi("2 477 ₽", "29 724 ₽")
val DemoNextPayment = NextPaymentUi("YouTube Premium · 04 ноя", "299 ₽", "youtube")

val DemoPausedHomeSubscriptions = DemoHomeSubscriptions.map { subscription ->
    if (subscription.id == "youtube") subscription.copy(status = SubscriptionStatus.PAUSED)
    else subscription
}
val DemoPausedHomeStatistics = HomeStatisticsUi("1 768 ₽", "21 216 ₽")
val DemoPausedNextPayment = NextPaymentUi("Яндекс Плюс · 04 ноя", "399 ₽", "yandex")
