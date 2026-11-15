package com.parzij.recurra.ui.subscriptions

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parzij.recurra.R
import com.parzij.recurra.ui.theme.*
import java.time.LocalDate

@Composable
fun CreateSubscriptionScreen(
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    onAddSubscription: ((AddSubscriptionForm) -> Unit)? = null,
) {
    var service by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable { mutableStateOf("") }
    var dateEpochDay by rememberSaveable { mutableStateOf<Long?>(null) }
    var period by rememberSaveable { mutableStateOf(BillingPeriod.MONTH) }
    AddSubscriptionContent(
        form =
            AddSubscriptionForm(service, amount, dateEpochDay?.let(LocalDate::ofEpochDay), period),
        onServiceChange = { service = it },
        onAmountChange = { amount = it },
        onDateChange = { dateEpochDay = it.toEpochDay() },
        onPeriodSelected = { period = it },
        onAddSubscription = onAddSubscription,
        onCancel = onCancel,
        modifier = modifier,
    )
}

@Composable
internal fun AddSubscriptionContent(
    form: AddSubscriptionForm,
    onServiceChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onPeriodSelected: (BillingPeriod) -> Unit,
    onAddSubscription: ((AddSubscriptionForm) -> Unit)?,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var submitted by rememberSaveable { mutableStateOf(false) }
    var handlerMissing by rememberSaveable { mutableStateOf(false) }
    val focus = LocalFocusManager.current

    Column(
        modifier
            .fillMaxSize()
            .background(RecurraBackground)
            .safeDrawingPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 30.dp, vertical = 30.dp)
    ) {
        Image(painterResource(R.drawable.logo_recurra), "Recurra", Modifier.size(153.dp, 38.dp))
        Spacer(Modifier.height(25.dp))
        Text(
            "Новая подписка",
            style =
                FieldTextStyle.copy(
                    fontSize = 24.sp,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
        )
        Spacer(Modifier.height(5.dp))
        Text(
            "Добавьте подписку, чтобы учитывать расходы",
            style =
                FieldTextStyle.copy(
                    fontSize = 15.sp,
                    lineHeight = 21.sp,
                    color = RecurraSlate,
                ),
        )
        Spacer(Modifier.height(50.dp))
        SubscriptionFormFields(
            form,
            onServiceChange,
            onAmountChange,
            onDateChange,
            onPeriodSelected,
            showErrors = submitted,
        )
        Spacer(Modifier.height(50.dp))
        SubscriptionPrimaryButton(
            text = "Добавить подписку",
            onClick = {
                focus.clearFocus()
                submitted = true
                handlerMissing = false
                if (form.isValid()) {
                    if (onAddSubscription != null) onAddSubscription(form)
                    else handlerMissing = true
                }
            },
        )
        if (handlerMissing) {
            Text(
                "Сохранение пока не подключено",
                Modifier.padding(top = 8.dp),
                style = FieldTextStyle.copy(fontSize = 14.sp, color = RecurraSlate),
            )
        }
        Spacer(Modifier.height(15.dp))
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                "Отмена",
                Modifier.clip(FieldShape)
                    .clickable(role = Role.Button, onClick = onCancel)
                    .heightIn(min = 48.dp)
                    .wrapContentHeight()
                    .padding(horizontal = 20.dp),
                style = FieldTextStyle.copy(color = RecurraRed),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddSubscriptionScreenPreview() {
    RecurraTheme {
        AddSubscriptionContent(
            AddSubscriptionForm("YouTube Premium", "299", LocalDate.of(2026, 11, 4)),
            {},
            {},
            {},
            {},
            {},
            {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BillingPeriodSelectorPreview() {
    RecurraTheme { BillingPeriodSelector(BillingPeriod.THREE_MONTHS, {}) }
}
