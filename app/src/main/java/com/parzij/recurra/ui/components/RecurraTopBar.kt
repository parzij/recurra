package com.parzij.recurra.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parzij.recurra.R
import com.parzij.recurra.ui.theme.OnestFontFamily
import com.parzij.recurra.ui.theme.RecurraPlum
import com.parzij.recurra.ui.welcome.Logo

enum class TopBarScreen {
    Home,
    Calendar,
    Profile,
}

@Composable
fun RecurraTopBar(
    screen: TopBarScreen,
    modifier: Modifier = Modifier,
    userName: String = "",
    onProfileClick: () -> Unit = {},
) {
    val name = userName.trim()
    val title =
        when (screen) {
            TopBarScreen.Home ->
                if (name.isEmpty()) {
                    stringResource(R.string.home_greeting_without_name)
                } else {
                    stringResource(R.string.home_greeting, name)
                }
            TopBarScreen.Calendar -> stringResource(R.string.calendar_title)
            TopBarScreen.Profile -> stringResource(R.string.profile_title)
        }
    val logoHeight = 38.dp
    val avatarSize = 48.dp

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 30.dp)
                .padding(top = 30.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Logo()
            val profileDescription = stringResource(R.string.profile_title)
            Box(
                modifier =
                    Modifier.size(avatarSize)
                        .clip(RoundedCornerShape(24.dp))
                        .background(RecurraPlum)
                        .clickable(role = Role.Button, onClick = onProfileClick)
                        .semantics { contentDescription = profileDescription },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = name.firstOrNull()?.uppercaseChar()?.toString().orEmpty(),
                    modifier = Modifier.clearAndSetSemantics {},
                    style =
                        MaterialTheme.typography.titleLarge.copy(
                            fontFamily = OnestFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 24.sp,
                        ),
                    color = colorResource(R.color.white),
                )
            }
        }
        val rowBelowLogo = avatarSize - logoHeight
        Spacer(Modifier.height(25.dp - rowBelowLogo))
        Text(
            text = title,
            modifier = Modifier.fillMaxWidth(),
            style =
                MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = OnestFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 24.sp,
                ),
            color = colorResource(R.color.black),
            textAlign = TextAlign.Start,
        )
    }
}
