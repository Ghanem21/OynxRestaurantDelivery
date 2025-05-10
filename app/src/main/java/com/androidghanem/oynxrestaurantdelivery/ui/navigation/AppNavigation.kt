package com.androidghanem.oynxrestaurantdelivery.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.androidghanem.oynxrestaurantdelivery.features.login.presentation.LoginScreen
import com.androidghanem.oynxrestaurantdelivery.ui.screens.home.HomeScreen
import com.androidghanem.oynxrestaurantdelivery.ui.screens.splash.SplashScreen
import com.androidghanem.oynxrestaurantdelivery.ui.screens.splash.SplashViewModel
import kotlinx.coroutines.flow.collectLatest

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
    
    LaunchedEffect(navController) {
        Log.d(tag, "Starting to collect session expiration events")
        try {
            SessionExpirationHandler.sessionExpiredEvent.collectLatest {
                Log.i(tag, "Session expiration event collected, navigating to login")
                SessionExpirationHandler.navigateToLogin(navController)
            }
        } catch (e: Exception) {
            Log.e(tag, "Error collecting session expiration events", e)
        }
    }
}

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Home : Screen("home")
}