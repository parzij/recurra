package com.parzij.recurra.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parzij.recurra.R
import com.parzij.recurra.ui.theme.RecurraCream
import com.parzij.recurra.ui.theme.RecurraPlum

enum class BottomBarDestination {
    HOME,
    PAYMENTS,
    PROFILE,
}

@Composable
fun RecurraBottomBar(
    selectedDestination: BottomBarDestination,
    onHomeClick: () -> Unit,
    onPaymentsClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(RecurraPlum)
                .navigationBarsPadding()
                .heightIn(min = 85.dp)
                .selectableGroup()
                .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (destination in BottomBarDestination.entries) {
            val selected = destination == selectedDestination
            val label =
                when (destination) {
                    BottomBarDestination.HOME -> R.string.bottom_bar_subscriptions
                    BottomBarDestination.PAYMENTS -> R.string.bottom_bar_payments
                    BottomBarDestination.PROFILE -> R.string.profile_title
                }
            val icon =
                when (destination) {
                    BottomBarDestination.HOME -> R.drawable.ic_home
                    BottomBarDestination.PAYMENTS ->
                        if (selected) R.drawable.ic_calendar_filled
                        else R.drawable.ic_calendar_outline_light
                    BottomBarDestination.PROFILE ->
                        if (selected) R.drawable.ic_user_filled
                        else R.drawable.ic_user_outline_light
                }
            val onClick =
                when (destination) {
                    BottomBarDestination.HOME -> onHomeClick
                    BottomBarDestination.PAYMENTS -> onPaymentsClick
                    BottomBarDestination.PROFILE -> onProfileClick
                }
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                BottomBarItem(selected, label, icon, onClick)
            }
        }
    }
}

@Composable
private fun BottomBarItem(
    selected: Boolean,
    @StringRes label: Int,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
) {
    val itemModifier =
        if (selected) {
            Modifier.widthIn(max = 110.dp)
                .fillMaxWidth()
                .heightIn(min = 65.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(RecurraCream)
        } else Modifier
    Column(
        modifier =
            itemModifier
                .selectable(
                    selected = selected,
                    role = Role.Tab,
                    onClick = { if (!selected) onClick() },
                )
                .then(
                    if (selected)
                        Modifier.padding(start = 10.dp, end = 10.dp, top = 6.dp, bottom = 5.dp)
                    else Modifier
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = if (selected) RecurraPlum else RecurraCream,
            modifier = Modifier.size(32.dp),
        )
        Text(
            text = stringResource(label),
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = if (selected) RecurraPlum else RecurraCream,
        )
    }
}
