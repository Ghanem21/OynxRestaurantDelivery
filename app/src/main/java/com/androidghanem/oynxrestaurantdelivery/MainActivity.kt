package com.androidghanem.oynxrestaurantdelivery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.androidghanem.oynxrestaurantdelivery.ui.navigation.AppNavigation
import com.androidghanem.oynxrestaurantdelivery.ui.theme.OynxRestaurantDeliveryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OynxRestaurantDeliveryTheme {
                AppNavigation()
            }
        }
    }
}