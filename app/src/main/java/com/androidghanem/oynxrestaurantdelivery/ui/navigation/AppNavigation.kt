package com.androidghanem.oynxrestaurantdelivery.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.androidghanem.oynxrestaurantdelivery.ui.screens.home.HomeScreen
import com.androidghanem.oynxrestaurantdelivery.ui.screens.login.LoginScreen
import com.androidghanem.oynxrestaurantdelivery.ui.screens.splash.SplashScreen
import com.androidghanem.oynxrestaurantdelivery.ui.screens.splash.SplashViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val splashViewModel: SplashViewModel = viewModel()
    
    SessionExpirationEffect(navController)

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    splashViewModel.onSplashFinished()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen()
        }
    }
}

/**
 * Effect that listens for session expiration events and navigates to login screen
 * 
 * Note: This effect will survive configuration changes since it's tied to the NavController's composition
 */
@Composable
private fun SessionExpirationEffect(navController: NavHostController) {
    val tag = "SessionExpiration"

    // Create a stable reference to the navController that can be used in LaunchedEffect
    val stableNavController = remember { navController }

    // Use a key of Unit to ensure this effect runs once per composition and survives recompositions
    LaunchedEffect(Unit) {
        Log.d(tag, "Starting to collect session expiration events")

        // Using supervisorScope to prevent cancellation of the effect if collection fails
        supervisorScope {
            try {
                // Use collectLatest to ensure we only process the most recent expiration event
                SessionExpirationHandler.sessionExpiredEvent.collectLatest {
                    Log.i(tag, "Session expiration event collected, navigating to login")

                    // Check if we're not already on the login screen
                    if (stableNavController.currentDestination?.route != Screen.Login.route) {
                        SessionExpirationHandler.navigateToLogin(stableNavController)
                    } else {
                        Log.d(tag, "Already on login screen, ignoring navigation")
                    }
                }
            } catch (e: Exception) {
                Log.e(tag, "Error collecting session expiration events", e)

                // Try to recover by setting up collection again after delay
                delay(1000)

                // Launch a new collection effort in case the first one failed
                launch {
                    try {
                        SessionExpirationHandler.sessionExpiredEvent.collect {
                            Log.i(
                                tag,
                                "Session expiration event collected (retry), navigating to login"
                            )
                            SessionExpirationHandler.navigateToLogin(stableNavController)
                        }
                    } catch (e: Exception) {
                        Log.e(tag, "Failed to recover session expiration collector", e)
                    }
                }
            }
        }
    }
}

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Home : Screen("home")
}