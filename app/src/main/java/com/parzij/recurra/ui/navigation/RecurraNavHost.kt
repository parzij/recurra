package com.parzij.recurra.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.parzij.recurra.BuildConfig
import com.parzij.recurra.ui.auth.login.LoginScreen
import com.parzij.recurra.ui.auth.register.RegisterScreen
import com.parzij.recurra.ui.home.DemoHomeSubscriptions
import com.parzij.recurra.ui.home.HomeScreen
import com.parzij.recurra.ui.home.SubscriptionUi
import com.parzij.recurra.ui.home.SubscriptionUiListSaver
import com.parzij.recurra.ui.payments.PaymentsScreen
import com.parzij.recurra.ui.profile.ProfileScreen
import com.parzij.recurra.ui.subscriptions.AddSubscriptionForm
import com.parzij.recurra.ui.subscriptions.CreateSubscriptionScreen
import com.parzij.recurra.ui.theme.NavigationBarAppearance
import com.parzij.recurra.ui.welcome.WelcomeScreen
import java.util.UUID

private object Routes {
    const val Welcome = "welcome"
    const val Register = "register"
    const val Login = "login"
    const val Main = "main"
    const val Home = "home"
    const val Payments = "payments"
    const val Profile = "profile"
    const val Add = "add"
}

@Composable
fun RecurraNavHost(
    onRegister: (String, String, String, String, Boolean) -> Unit = { _, _, _, _, _ -> },
    onLogin: (String, String) -> Unit = { _, _ -> },
    enableDebugUiDemo: Boolean = BuildConfig.DEBUG,
    onLogout: () -> Unit = {},
    onAddSubscription: ((AddSubscriptionForm) -> Unit)? = null,
) {
    val navController = rememberNavController()
    val uiDemo = BuildConfig.DEBUG && enableDebugUiDemo

    var userName by rememberSaveable { mutableStateOf("") }
    var userEmail by rememberSaveable { mutableStateOf("") }
    var subscriptions by
        rememberSaveable(uiDemo, stateSaver = SubscriptionUiListSaver) {
            mutableStateOf(if (uiDemo) DemoHomeSubscriptions else emptyList())
        }
    val entry by navController.currentBackStackEntryAsState()
    val mainScreen =
        entry?.destination?.route in setOf(Routes.Home, Routes.Payments, Routes.Profile)
    NavigationBarAppearance(mainScreen)

    NavHost(navController = navController, startDestination = Routes.Welcome) {
        composable(Routes.Welcome) {
            WelcomeScreen(
                onStartClick = { navController.navigateAuth(Routes.Register) },
                onLoginClick = { navController.navigateAuth(Routes.Login) },
            )
        }
        composable(Routes.Register) {
            RegisterScreen(
                onBackClick = { navController.popBackStack() },
                onLoginClick = { navController.navigateAuth(Routes.Login) },
                onRegisterClick = { name, email, password, confirmation, accepted ->
                    onRegister(name, email, password, confirmation, accepted)
                    if (uiDemo) {
                        userName = name.trim()
                        userEmail = email.trim()
                        navController.navigateMain()
                    }
                },
            )
        }
        composable(Routes.Login) {
            LoginScreen(
                onBackClick = {
                    if (!navController.popBackStack()) {
                        navController.navigate(Routes.Welcome) { launchSingleTop = true }
                    }
                },
                onRegisterClick = { navController.navigateAuth(Routes.Register) },
                onLoginClick = { email, password ->
                    onLogin(email, password)
                    if (uiDemo) {
                        userEmail = email.trim()
                        userName = ""
                        navController.navigateMain()
                    }
                },
            )
        }

        if (uiDemo) {
            composable(Routes.Add) {
                CreateSubscriptionScreen(
                    onCancel = { navController.popBackStack() },
                    onAddSubscription = { form ->
                        onAddSubscription?.invoke(form)
                        subscriptions =
                            subscriptions +
                                SubscriptionUi(
                                    id = UUID.randomUUID().toString(),
                                    name = form.service,
                                    price = "${form.amount} ₽",
                                    initial =
                                        form.service
                                            .trim()
                                            .firstOrNull()
                                            ?.uppercaseChar()
                                            ?.toString()
                                            .orEmpty(),
                                    billingPeriod = form.period,
                                    billingDate = form.date,
                                )
                        navController.popBackStack()
                    },
                )
            }
            navigation(startDestination = Routes.Home, route = Routes.Main) {
                composable(Routes.Home) {
                    HomeScreen(
                        userName = userName,
                        subscriptions = subscriptions,
                        onSubscriptionsChange = { subscriptions = it },
                        onHomeClick = { navController.navigateTab(Routes.Home) },
                        onPaymentsClick = { navController.navigateTab(Routes.Payments) },
                        onProfileClick = { navController.navigateTab(Routes.Profile) },
                        onAddClick = {
                            navController.navigate(Routes.Add) { launchSingleTop = true }
                        },
                    )
                }
                composable(Routes.Payments) {
                    PaymentsScreen(
                        userName = userName,
                        onHomeClick = { navController.navigateTab(Routes.Home) },
                        onPaymentsClick = { navController.navigateTab(Routes.Payments) },
                        onProfileClick = { navController.navigateTab(Routes.Profile) },
                        onAddClick = {
                            navController.navigate(Routes.Add) { launchSingleTop = true }
                        },
                    )
                }
                composable(Routes.Profile) {
                    ProfileScreen(
                        userName = userName,
                        userEmail = userEmail,
                        subscriptionsCount = subscriptions.size,
                        onLogout = {
                            onLogout()
                            userName = ""
                            userEmail = ""
                            subscriptions = DemoHomeSubscriptions

                            listOf(
                                    Routes.Main,
                                    Routes.Home,
                                    Routes.Payments,
                                    Routes.Profile,
                                    Routes.Add,
                                    Routes.Register,
                                    Routes.Login,
                                )
                                .forEach {
                                    navController.clearBackStack(it)
                                }
                            navController.navigate(Routes.Login) {
                                popUpTo(navController.graph.id)
                                launchSingleTop = true
                            }
                        },
                        onHomeClick = { navController.navigateTab(Routes.Home) },
                        onPaymentsClick = { navController.navigateTab(Routes.Payments) },
                        onProfileClick = { navController.navigateTab(Routes.Profile) },
                        onAddClick = {
                            navController.navigate(Routes.Add) { launchSingleTop = true }
                        },
                    )
                }
            }
        }
    }
}

private fun NavHostController.navigateMain() {
    navigate(Routes.Main) {
        popUpTo(graph.id)
        launchSingleTop = true
    }
}

private fun NavHostController.navigateAuth(route: String) {
    if (currentDestination?.route == route) return
    navigate(route) {
        popUpTo(Routes.Welcome) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

private fun NavHostController.navigateTab(route: String) {
    if (currentDestination?.route == route) return
    navigate(route) {
        popUpTo(Routes.Home) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
