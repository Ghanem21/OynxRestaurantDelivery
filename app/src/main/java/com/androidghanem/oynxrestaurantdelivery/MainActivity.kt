package com.androidghanem.oynxrestaurantdelivery

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.androidghanem.oynxrestaurantdelivery.data.preferences.AppPreferencesManager
import com.androidghanem.oynxrestaurantdelivery.ui.navigation.AppNavigation
import com.androidghanem.oynxrestaurantdelivery.ui.theme.OynxRestaurantDeliveryTheme
import com.androidghanem.oynxrestaurantdelivery.utils.LocaleHelper
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var preferencesManager: AppPreferencesManager

    override fun attachBaseContext(newBase: Context) {
        preferencesManager = AppPreferencesManager(newBase)
        val languageCode = preferencesManager.getLanguageCode()
        super.attachBaseContext(LocaleHelper.setLocale(newBase, languageCode))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OynxRestaurantDeliveryTheme {
                AppNavigation()
            }
        }

        lifecycleScope.launch {
            preferencesManager.languageCode.collect { newLanguageCode ->
                runOnUiThread { 
                    LocaleHelper.setLocale(this@MainActivity, newLanguageCode)
                    recreate() 
                }
            }
        }
    }
}