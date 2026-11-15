package com.parzij.recurra.ui.auth

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parzij.recurra.R
import com.parzij.recurra.ui.theme.*

private val AuthHeaderHeight = 48.dp
private val AuthLogoHeight = 38.dp

@Composable
internal fun AuthLayout(
    @StringRes title: Int,
    @StringRes subtitle: Int,
    @StringRes footerPrompt: Int,
    @StringRes footerAction: Int,
    onBackClick: () -> Unit,
    onFooterClick: () -> Unit,
    logoToContentSpacing: Dp,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(RecurraBackground)
                .safeDrawingPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(top = 6.dp, bottom = 24.dp)
    ) {
        AuthHeader(onBackClick = onBackClick)

        val logoBottomInset = (AuthHeaderHeight - AuthLogoHeight) / 2
        Spacer(Modifier.height(logoToContentSpacing - logoBottomInset))
        Column(Modifier.fillMaxWidth().padding(horizontal = 39.dp)) {
            Text(
                stringResource(title),
                modifier = Modifier.fillMaxWidth(),
                style =
                    MaterialTheme.typography.headlineLarge.copy(
                        fontFamily = OnestFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 34.sp,
                    ),
                color = colorResource(R.color.black),
                textAlign = TextAlign.Start,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(subtitle),
                modifier = Modifier.fillMaxWidth(),
                style =
                    MaterialTheme.typography.titleMedium.copy(
                        fontFamily = OnestFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp,
                    ),
                color = RecurraSlate,
                textAlign = TextAlign.Start,
            )
            Spacer(Modifier.height(40.dp))
            content()
            Spacer(Modifier.height(20.dp))
            AuthFooter(footerPrompt, footerAction, onFooterClick)
        }
    }
}

@Composable
internal fun AuthHeader(onBackClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().heightIn(min = AuthHeaderHeight)) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 17.dp),
        ) {
            Icon(
                painterResource(R.drawable.ic_arrow_left),
                contentDescription = stringResource(R.string.auth_back),
                tint = RecurraNavy,
                modifier = Modifier.size(32.dp),
            )
        }
        Image(
            painterResource(R.drawable.logo_recurra),
            contentDescription = stringResource(R.string.app_name),
            modifier =
                Modifier.align(Alignment.Center).size(width = 153.dp, height = AuthLogoHeight),
        )
    }
}

@Composable
internal fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes placeholder: Int,
    @DrawableRes leadingIcon: Int,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    val fieldLabel = stringResource(placeholder)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth().semantics { contentDescription = fieldLabel },
        placeholder = { Text(stringResource(placeholder)) },
        leadingIcon = {
            Icon(
                painterResource(leadingIcon),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
        },
        trailingIcon = trailingIcon,
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedContainerColor = RecurraWelcomeCard,
                unfocusedContainerColor = RecurraWelcomeCard,
                focusedBorderColor = RecurraPlum,
                unfocusedBorderColor = RecurraSand,
                focusedTextColor = RecurraNavy,
                unfocusedTextColor = RecurraNavy,
                focusedLeadingIconColor = RecurraNavy,
                unfocusedLeadingIconColor = RecurraNavy,
                focusedPlaceholderColor = RecurraSlate,
                unfocusedPlaceholderColor = RecurraSlate,
                cursorColor = RecurraPlum,
            ),
    )
}

@Composable
internal fun AuthPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes placeholder: Int,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Done,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    var visible by rememberSaveable { mutableStateOf(false) }
    val fieldName = stringResource(placeholder)
    AuthTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        leadingIcon = R.drawable.ic_lock_keyhole,
        modifier = modifier,
        keyboardOptions =
            KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction),
        keyboardActions = keyboardActions,
        visualTransformation =
            if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    painterResource(if (visible) R.drawable.ic_eye else R.drawable.ic_eye_slash),
                    contentDescription =
                        stringResource(
                            if (visible) R.string.auth_hide_password
                            else R.string.auth_show_password,
                            fieldName,
                        ),
                    tint = RecurraNavy,
                    modifier = Modifier.size(24.dp),
                )
            }
        },
    )
}

@Composable
internal fun AuthButton(@StringRes text: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().heightIn(min = 48.dp),
        shape = RoundedCornerShape(18.dp),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = RecurraPlum,
                contentColor = colorResource(R.color.white),
            ),
    ) {
        Text(stringResource(text), style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
internal fun AuthFooter(
    @StringRes prompt: Int,
    @StringRes action: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val textStyle =
        MaterialTheme.typography.bodyMedium.copy(
            fontFamily = OnestFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
        )
    FlowRow(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
        itemVerticalAlignment = Alignment.CenterVertically,
    ) {
        Text(stringResource(prompt), color = RecurraSlate, style = textStyle)
        Text(
            stringResource(action),
            modifier = Modifier.clickable(role = Role.Button, onClick = onClick),
            color = RecurraTeal,
            style = textStyle,
        )
    }
}

@Composable
internal fun AuthConsent(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val text = buildAnnotatedString {
        append(stringResource(R.string.auth_consent_prefix))
        withStyle(SpanStyle(color = RecurraTeal)) { append(stringResource(R.string.auth_terms)) }
        append(stringResource(R.string.auth_consent_and))
        withStyle(SpanStyle(color = RecurraTeal)) { append(stringResource(R.string.auth_privacy)) }
    }
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .toggleable(value = checked, role = Role.Checkbox, onValueChange = onCheckedChange),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Image(
            painterResource(
                if (checked) R.drawable.ic_tick_square_checked
                else R.drawable.ic_tick_square_unchecked
            ),
            contentDescription = null,
            modifier = Modifier.size(32.dp),
        )
        Text(
            text,
            modifier = Modifier.weight(1f),
            style =
                MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = OnestFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                ),
            color = RecurraSlate,
        )
    }
}
