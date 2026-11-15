package com.parzij.recurra.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parzij.recurra.R
import com.parzij.recurra.ui.components.BottomBarDestination
import com.parzij.recurra.ui.components.MainScreenScaffold
import com.parzij.recurra.ui.components.RecurraTopBar
import com.parzij.recurra.ui.components.TopBarScreen
import com.parzij.recurra.ui.theme.RecurraPlum
import com.parzij.recurra.ui.theme.RecurraSand
import com.parzij.recurra.ui.theme.RecurraSlate
import com.parzij.recurra.ui.theme.RecurraTheme

@Composable
fun ProfileScreen(
    userName: String,
    userEmail: String,
    subscriptionsCount: Int,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onPaymentsClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
) {
    BoxWithConstraints(modifier) {
        val scrollHeader = maxHeight < 500.dp
        MainScreenScaffold(
            destination = BottomBarDestination.PROFILE,
            modifier = Modifier.fillMaxSize(),
            showTopBar = !scrollHeader,
            userName = userName,
            onProfileClick = onProfileClick,
            onHomeClick = onHomeClick,
            onPaymentsClick = onPaymentsClick,
            onAddClick = onAddClick,
        ) {
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                if (scrollHeader) {
                    RecurraTopBar(
                        TopBarScreen.Profile,
                        userName = userName,
                        onProfileClick = onProfileClick,
                    )
                }
                Column(Modifier.padding(horizontal = 30.dp).padding(top = 24.dp, bottom = 120.dp)) {
                    UserInformation(userName, userEmail)
                    Spacer(Modifier.height(50.dp))
                    SubscriptionsCountCard(subscriptionsCount)
                    Spacer(Modifier.height(13.dp))
                    Button(
                        onClick = onLogout,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 55.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = RecurraPlum,
                                contentColor = colorResource(R.color.white),
                            ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    ) {
                        Text(
                            stringResource(R.string.profile_logout),
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 20.sp,
                            lineHeight = 26.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UserInformation(userName: String, userEmail: String) {
    Row(
        modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp),
        horizontalArrangement = Arrangement.spacedBy(25.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.logo_android),
            contentDescription = null,
            modifier = Modifier.size(64.dp).clip(CircleShape),
            contentScale = ContentScale.Fit,
        )
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                userName,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.black),
            )
            Text(
                userEmail,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium,
                color = RecurraSlate,
            )
        }
    }
}

@Composable
private fun SubscriptionsCountCard(subscriptionsCount: Int) {
    Row(
        modifier =
            Modifier.fillMaxWidth()
                .border(BorderStroke(2.dp, RecurraSand), RoundedCornerShape(16.dp))
                .heightIn(min = 55.dp)
                .semantics(mergeDescendants = true) {}
                .padding(horizontal = 28.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_shop),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = colorResource(R.color.black),
        )
        Text(
            stringResource(R.string.profile_subscriptions_count),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            fontSize = 18.sp,
            lineHeight = 23.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorResource(R.color.black),
        )
        Text(
            subscriptionsCount.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontSize = 18.sp,
            lineHeight = 23.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorResource(R.color.black),
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    RecurraTheme {
        ProfileScreen(
            userName = "Вячеслав",
            userEmail = "parzij@example.com",
            subscriptionsCount = 4,
            onLogout = {},
        )
    }
}
