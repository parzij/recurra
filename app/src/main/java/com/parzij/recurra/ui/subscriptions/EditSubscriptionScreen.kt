package com.parzij.recurra.ui.subscriptions

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parzij.recurra.R
import com.parzij.recurra.ui.home.SubscriptionStatus
import com.parzij.recurra.ui.home.SubscriptionUi
import com.parzij.recurra.ui.theme.*
import java.time.LocalDate
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSubscriptionBottomSheet(
    subscription: SubscriptionUi,
    onDismiss: () -> Unit,
    onSave: (SubscriptionUi) -> Boolean,
    onPause: (String) -> Unit,
    onDelete: (String) -> Unit,
    onResume: (String) -> Unit,
) {
    key(subscription.id) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val scope = rememberCoroutineScope()
        val focus = LocalFocusManager.current
        val close: () -> Unit = {
            focus.clearFocus()
            scope.launch {
                sheetState.hide()
                onDismiss()
            }
        }
        var service by rememberSaveable { mutableStateOf(subscription.name) }
        var amount by rememberSaveable {
            mutableStateOf(subscription.price.replace("₽", "").filterNot(Char::isWhitespace))
        }
        var dateEpochDay by rememberSaveable {
            mutableStateOf(subscription.billingDate?.toEpochDay())
        }
        var period by rememberSaveable { mutableStateOf(subscription.billingPeriod) }
        var submitted by rememberSaveable { mutableStateOf(false) }
        var saveRejected by rememberSaveable { mutableStateOf(false) }
        val form =
            AddSubscriptionForm(service, amount, dateEpochDay?.let(LocalDate::ofEpochDay), period)

        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            sheetMaxWidth = Dp.Unspecified,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            containerColor = RecurraBackground,
            dragHandle = null,
        ) {
            Column(
                Modifier.fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 30.dp)
                    .padding(top = 25.dp, bottom = 25.dp)
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Редактировать подписку",
                        Modifier.weight(1f),
                        style =
                            FieldTextStyle.copy(
                                fontSize = 24.sp,
                                lineHeight = 30.sp,
                                fontWeight = FontWeight.SemiBold,
                            ),
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = close, modifier = Modifier.size(24.dp)) {
                        Icon(
                            painterResource(R.drawable.ic_arrow_down),
                            "Закрыть редактирование",
                            Modifier.size(24.dp),
                            tint = Color.Black,
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))
                SubscriptionFormFields(
                    form = form,
                    onServiceChange = { service = it },
                    onAmountChange = { amount = it },
                    onDateChange = { dateEpochDay = it.toEpochDay() },
                    onPeriodSelected = { period = it },
                    showErrors = submitted,
                )
                Spacer(Modifier.height(30.dp))
                SubscriptionPrimaryButton(
                    "Сохранить изменения",
                    onClick = {
                        focus.clearFocus()
                        submitted = true
                        saveRejected = false
                        if (form.isValid()) {
                            val updated =
                                subscription.copy(
                                    name = service,
                                    price = "$amount ₽",
                                    billingDate = form.date,
                                    billingPeriod = period,
                                )
                            if (onSave(updated)) close() else saveRejected = true
                        }
                    },
                )
                if (saveRejected) {
                    Text(
                        "Не удалось применить изменения",
                        Modifier.padding(top = 8.dp),
                        style = FieldTextStyle.copy(fontSize = 14.sp, color = RecurraRed),
                    )
                }
                Spacer(Modifier.height(15.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    val paused = subscription.status == SubscriptionStatus.PAUSED
                    SubscriptionManagementButton(
                        text = if (paused) "Возобновить" else "Приостановить",
                        icon =
                            if (paused) R.drawable.ic_play_circle else R.drawable.ic_pause_circle,
                        color = if (paused) RecurraTeal else RecurraSlate,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (paused) onResume(subscription.id) else onPause(subscription.id)
                            close()
                        },
                    )
                    SubscriptionManagementButton(
                        "Удалить",
                        R.drawable.ic_trash,
                        RecurraRed,
                        Modifier.weight(1f),
                        onClick = {
                            onDelete(subscription.id)
                            close()
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun SubscriptionManagementButton(
    text: String,
    @DrawableRes icon: Int,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 55.dp),
        shape = FieldShape,
        border = BorderStroke(2.dp, color),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = color),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp),
    ) {
        Icon(
            painterResource(icon),
            null,
            Modifier.size(if (icon == R.drawable.ic_play_circle) 24.dp else 22.dp),
            tint = color,
        )
        Spacer(Modifier.width(6.dp))
        Text(text, style = FieldTextStyle.copy(fontSize = 14.sp, lineHeight = 18.sp, color = color))
    }
}

@Preview(showBackground = true)
@Composable
fun EditSubscriptionBottomSheetPreview() {
    RecurraTheme {
        EditSubscriptionBottomSheet(
            subscription =
                SubscriptionUi(
                    "youtube",
                    "YouTube Premium",
                    "299 ₽",
                    "Y",
                    billingDate = LocalDate.of(2026, 11, 4),
                ),
            onDismiss = {},
            onSave = { true },
            onPause = {},
            onDelete = {},
            onResume = {},
        )
    }
}
