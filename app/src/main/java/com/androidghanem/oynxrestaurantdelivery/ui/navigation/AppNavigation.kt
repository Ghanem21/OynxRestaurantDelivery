package com.androidghanem.oynxrestaurantdelivery.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.androidghanem.oynxrestaurantdelivery.ui.screens.home.HomeScreen
import com.androidghanem.oynxrestaurantdelivery.ui.screens.login.LoginScreen
import com.androidghanem.oynxrestaurantdelivery.ui.screens.splash.SplashScreen
import com.androidghanem.oynxrestaurantdelivery.ui.screens.splash.SplashViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val splashViewModel: SplashViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    splashViewModel.onSplashFinished()
                    navController.navigate(Screen.Login.route) {
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

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Home : Screen("home")
}