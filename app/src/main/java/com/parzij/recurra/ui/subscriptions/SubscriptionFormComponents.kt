package com.parzij.recurra.ui.subscriptions

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parzij.recurra.R
import com.parzij.recurra.ui.theme.*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class BillingPeriod(val months: Int, val label: String) {
    MONTH(1, "месяц"),
    THREE_MONTHS(3, "3 месяца"),
    SIX_MONTHS(6, "6 месяцев"),
    YEAR(12, "год");

    val homeLabel: String
        get() =
            when (this) {
                MONTH -> "в месяц"
                THREE_MONTHS -> "за 3 месяца"
                SIX_MONTHS -> "за 6 месяцев"
                YEAR -> "в год"
            }
}

data class AddSubscriptionForm(
    val service: String = "",
    val amount: String = "",
    val date: LocalDate? = null,
    val period: BillingPeriod = BillingPeriod.MONTH,
)

internal val FieldShape = RoundedCornerShape(16.dp)
private val DateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.ROOT)
internal val FieldTextStyle =
    TextStyle(
        fontFamily = OnestFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        color = Color.Black,
    )

internal fun AddSubscriptionForm.hasValidAmount(): Boolean =
    amount.replace(',', '.').let {
        it.matches(Regex("[0-9]+([.][0-9]+)?")) && it.toBigDecimalOrNull()?.signum() == 1
    }

internal fun AddSubscriptionForm.isValid(): Boolean =
    service.isNotBlank() && hasValidAmount() && date != null

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SubscriptionFormFields(
    form: AddSubscriptionForm,
    onServiceChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onPeriodSelected: (BillingPeriod) -> Unit,
    showErrors: Boolean = false,
) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    val focus = LocalFocusManager.current
    val serviceError = showErrors && form.service.isBlank()
    val amountError = showErrors && !form.hasValidAmount()
    val dateError = showErrors && form.date == null
    Column {
        FormField("Сервис", if (serviceError) "Введите название сервиса" else null) {
            SubscriptionTextField(form.service, onServiceChange, "Сервис", "Название сервиса")
        }
        Spacer(Modifier.height(15.dp))
        FormField("Сумма списания", if (amountError) "Введите сумму больше нуля" else null) {
            SubscriptionTextField(
                form.amount,
                onAmountChange,
                "Сумма списания",
                "",
                suffix = "₽",
                icon = R.drawable.ic_receipt_text,
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done,
                    ),
                keyboardActions = KeyboardActions(onDone = { focus.clearFocus() }),
            )
        }
        Spacer(Modifier.height(15.dp))
        FormField("Дата списания", if (dateError) "Выберите дату списания" else null) {
            Row(
                Modifier.fillMaxWidth()
                    .heightIn(min = 55.dp)
                    .border(2.dp, RecurraSand, FieldShape)
                    .clip(FieldShape)
                    .clickable(role = Role.Button) {
                        focus.clearFocus()
                        showDatePicker = true
                    }
                    .semantics { contentDescription = "Дата списания" }
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    form.date?.format(DateFormat) ?: "Выберите дату",
                    Modifier.weight(1f),
                    style = FieldTextStyle,
                    color = if (form.date == null) RecurraSlate else Color.Black,
                )
                Spacer(Modifier.width(12.dp))
                Icon(
                    painterResource(R.drawable.ic_calendar_outline_dark),
                    null,
                    Modifier.size(24.dp),
                    tint = Color.Unspecified,
                )
            }
        }
        Spacer(Modifier.height(15.dp))
        FormField("Периодичность") { BillingPeriodSelector(form.period, onPeriodSelected) }
    }
    if (showDatePicker) {
        val context = LocalContext.current
        val configuration = LocalConfiguration.current
        val russianConfiguration =
            remember(configuration) {
                Configuration(configuration).apply { setLocale(Locale.forLanguageTag("ru-RU")) }
            }
        val russianContext =
            remember(context, russianConfiguration) {
                context.createConfigurationContext(russianConfiguration)
            }
        CompositionLocalProvider(
            LocalContext provides russianContext,
            LocalConfiguration provides russianConfiguration,
            LocalResources provides russianContext.resources,
        ) {
            val picker =
                rememberDatePickerState(
                    initialSelectedDateMillis =
                        form.date?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
                )
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        enabled = picker.selectedDateMillis != null,
                        onClick = {
                            picker.selectedDateMillis?.let {
                                onDateChange(
                                    Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
                                )
                            }
                            showDatePicker = false
                        },
                    ) {
                        Text("Выбрать")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Отмена") }
                },
            ) {
                CompositionLocalProvider(
                    LocalContext provides russianContext,
                    LocalConfiguration provides russianConfiguration,
                    LocalResources provides russianContext.resources,
                ) {
                    DatePicker(
                        state = picker,
                        dateFormatter =
                            remember {
                                DatePickerDefaults.dateFormatter(selectedDateSkeleton = "ddMMyyyy")
                            },
                    )
                }
            }
        }
    }
}

@Composable
internal fun SubscriptionPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().heightIn(min = 55.dp),
        shape = FieldShape,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
        colors =
            ButtonDefaults.buttonColors(containerColor = RecurraPlum, contentColor = Color.White),
    ) {
        Text(
            text,
            style =
                FieldTextStyle.copy(
                    color = Color.White,
                    fontSize = 20.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun FormField(label: String, error: String? = null, content: @Composable () -> Unit) {
    Text(
        label,
        style = FieldTextStyle.copy(fontSize = 15.sp, lineHeight = 20.sp),
        color = RecurraSlate,
    )
    Spacer(Modifier.height(8.dp))
    content()
    if (error != null)
        Text(
            error,
            Modifier.padding(top = 4.dp),
            style = FieldTextStyle.copy(fontSize = 13.sp, lineHeight = 18.sp, color = RecurraRed),
        )
}

@Composable
private fun SubscriptionTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    @DrawableRes icon: Int? = null,
    suffix: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = FieldTextStyle,
        cursorBrush = SolidColor(RecurraPlum),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        modifier =
            Modifier.fillMaxWidth()
                .heightIn(min = 55.dp)
                .border(2.dp, RecurraSand, FieldShape)
                .semantics { contentDescription = label },
        decorationBox = { inner ->
            Row(
                Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (suffix != null) {
                    Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.weight(1f, fill = false).width(IntrinsicSize.Min)) { inner() }
                        Text(
                            if (value.isEmpty()) suffix else " $suffix",
                            style = FieldTextStyle,
                            color = Color.Black,
                        )
                    }
                } else {
                    Box(Modifier.weight(1f)) {
                        if (value.isEmpty())
                            Text(placeholder, style = FieldTextStyle, color = RecurraSlate)
                        inner()
                    }
                }
                if (icon != null) {
                    Spacer(Modifier.width(12.dp))
                    Icon(
                        painterResource(icon),
                        null,
                        Modifier.size(24.dp),
                        tint = Color.Unspecified,
                    )
                }
            }
        },
    )
}

@Composable
fun BillingPeriodSelector(
    selectedPeriod: BillingPeriod,
    onPeriodSelected: (BillingPeriod) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .heightIn(min = 55.dp)
            .border(2.dp, RecurraSand, FieldShape)
            .clip(FieldShape)
            .selectableGroup()
            .padding(horizontal = 4.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BillingPeriod.entries.forEach { period ->
            val selected = period == selectedPeriod
            Box(
                Modifier.weight(1f)
                    .heightIn(min = 45.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selected) RecurraPlum else Color.Transparent)
                    .selectable(
                        selected = selected,
                        role = Role.Tab,
                        onClick = { onPeriodSelected(period) },
                    )
                    .padding(horizontal = 2.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    period.label,
                    style = FieldTextStyle.copy(fontSize = 15.sp, lineHeight = 20.sp),
                    color = if (selected) Color.White else RecurraSlate,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
