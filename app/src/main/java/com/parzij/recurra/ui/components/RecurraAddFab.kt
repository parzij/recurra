package com.parzij.recurra.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.parzij.recurra.R
import com.parzij.recurra.ui.theme.RecurraCream
import com.parzij.recurra.ui.theme.RecurraTeal

@Composable
fun RecurraAddFab(onAddClick: () -> Unit, modifier: Modifier = Modifier) {
    FloatingActionButton(
        onClick = onAddClick,
        modifier = modifier.size(64.dp),
        shape = CircleShape,
        containerColor = RecurraTeal,
        contentColor = RecurraCream,
        elevation =
            FloatingActionButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                focusedElevation = 0.dp,
                hoveredElevation = 0.dp,
            ),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_add),
            contentDescription = stringResource(R.string.add_subscription),
            modifier = Modifier.size(48.dp),
        )
    }
}
