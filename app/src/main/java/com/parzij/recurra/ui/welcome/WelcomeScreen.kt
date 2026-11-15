package com.parzij.recurra.ui.welcome

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parzij.recurra.R
import com.parzij.recurra.ui.theme.OnestFontFamily
import com.parzij.recurra.ui.theme.RecurraBackground
import com.parzij.recurra.ui.theme.RecurraNavy
import com.parzij.recurra.ui.theme.RecurraOrange
import com.parzij.recurra.ui.theme.RecurraPlum
import com.parzij.recurra.ui.theme.RecurraRed
import com.parzij.recurra.ui.theme.RecurraSand
import com.parzij.recurra.ui.theme.RecurraSlate
import com.parzij.recurra.ui.theme.RecurraTeal
import com.parzij.recurra.ui.theme.RecurraTheme
import com.parzij.recurra.ui.theme.RecurraWelcomeCard
import com.parzij.recurra.ui.theme.RecurraWelcomeMint
import com.parzij.recurra.ui.theme.RecurraWelcomeRose
import com.parzij.recurra.ui.theme.RecurraWelcomeWallet

@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    onStartClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(RecurraBackground)
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(top = 6.dp, bottom = 15.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp)) {
            Logo()
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 39.dp).padding(top = 40.dp)
        ) {
            WelcomeHeader(modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(15.dp))

            MonthlyExpensesCard()

            Spacer(modifier = Modifier.height(20.dp))

            SubscriptionsCard()

            Spacer(modifier = Modifier.height(20.dp))

            FeaturesCard()

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onStartClick,
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                shape = RoundedCornerShape(18.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = RecurraPlum,
                        contentColor = RecurraBackground,
                    ),
            ) {
                Text(
                    text = stringResource(R.string.welcome_start),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            ) {
                Text(
                    text = stringResource(R.string.welcome_has_account),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = RecurraSlate,
                )

                Text(
                    text = stringResource(R.string.welcome_login),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = RecurraTeal,
                    modifier =
                        Modifier.clickable(
                            role = Role.Button,
                            onClick = onLoginClick,
                        ),
                )
            }
        }
    }
}

@Composable
fun Logo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.logo_recurra),
        contentDescription = stringResource(id = R.string.app_name),
        modifier = modifier.size(width = 153.dp, height = 38.dp),
    )
}

@Composable
fun WelcomeHeader(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(id = R.string.welcome_text_main),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
            fontSize = 40.sp,
            lineHeight = 50.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.black),
        )

        Text(
            text = stringResource(R.string.welcome_subtitle),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
            fontSize = 21.sp,
            lineHeight = 26.sp,
            color = RecurraPlum,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
fun MonthlyExpensesCard(modifier: Modifier = Modifier) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = 73.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(RecurraPlum)
                .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier =
                Modifier.size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(RecurraWelcomeWallet),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_empty_wallet),
                contentDescription = null,
                tint = RecurraSand,
                modifier = Modifier.size(32.dp),
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = stringResource(id = R.string.welcome_monthly_expenses_title),
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = RecurraBackground,
            )

            Text(
                text = stringResource(id = R.string.welcome_monthly_expenses_price),
                fontSize = 27.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.Bold,
                color = RecurraBackground,
            )
        }
    }
}

@Composable
fun SubscriptionsCard(modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(RecurraWelcomeCard)
                .border(
                    width = 2.dp,
                    color = RecurraSand,
                    shape = RoundedCornerShape(20.dp),
                )
                .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SubscriptionRow(
            initial = stringResource(R.string.welcome_youtube_initial),
            name = stringResource(R.string.welcome_youtube),
            price = stringResource(R.string.welcome_youtube_price),
            iconColor = RecurraRed,
        )

        SubscriptionRow(
            initial = stringResource(R.string.welcome_yandex_initial),
            name = stringResource(R.string.welcome_yandex),
            price = stringResource(R.string.welcome_yandex_price),
            iconColor = RecurraOrange,
        )

        SubscriptionRow(
            initial = stringResource(R.string.welcome_okko_initial),
            name = stringResource(R.string.welcome_okko),
            price = stringResource(R.string.welcome_okko_price),
            iconColor = RecurraNavy,
        )

        SubscriptionRow(
            initial = stringResource(R.string.welcome_spotify_initial),
            name = stringResource(R.string.welcome_spotify),
            price = stringResource(R.string.welcome_spotify_price),
            iconColor = RecurraTeal,
        )
    }
}

@Composable
fun SubscriptionRow(
    name: String,
    price: String,
    iconColor: Color,
    modifier: Modifier = Modifier,
    initial: String = "",
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(38.dp).background(iconColor, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = initial,
                modifier = Modifier.clearAndSetSemantics {},
                style =
                    TextStyle(
                        fontFamily = OnestFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        letterSpacing = 0.sp,
                        textAlign = TextAlign.Center,
                    ),
                color = colorResource(R.color.white),
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = name,
            modifier = Modifier.weight(1f),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorResource(R.color.black),
        )

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = price,
                fontSize = 20.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.black),
            )

            Text(
                text = stringResource(R.string.welcome_per_month),
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = RecurraSlate,
            )
        }
    }
}

@Composable
fun FeaturesCard(modifier: Modifier = Modifier) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(RecurraWelcomeCard)
                .border(
                    width = 2.dp,
                    color = RecurraSand,
                    shape = RoundedCornerShape(20.dp),
                )
                .padding(horizontal = 8.dp, vertical = 18.dp),
        verticalAlignment = Alignment.Top,
    ) {
        FeatureItem(
            icon = R.drawable.ic_refresh_circle,
            title = stringResource(R.string.welcome_feature_subscriptions),
            backgroundColor = RecurraSand,
            modifier = Modifier.weight(1f),
        )

        FeatureItem(
            icon = R.drawable.ic_tick_circle,
            title = stringResource(R.string.welcome_feature_payments),
            backgroundColor = RecurraWelcomeMint,
            modifier = Modifier.weight(1f),
        )

        FeatureItem(
            icon = R.drawable.ic_chart,
            title = stringResource(R.string.welcome_feature_statistics),
            backgroundColor = RecurraWelcomeRose,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
fun FeatureItem(
    @DrawableRes icon: Int,
    title: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(36.dp).background(backgroundColor, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier =
                    Modifier.size(if (icon == R.drawable.ic_refresh_circle) 36.dp else 24.dp),
            )
        }

        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 16.sp,
            textAlign = TextAlign.Center,
            color = colorResource(R.color.black),
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun WelcomeScreenPreview() {
    RecurraTheme {
        WelcomeScreen()
    }
}
