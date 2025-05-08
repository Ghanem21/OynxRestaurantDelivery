package com.androidghanem.oynxrestaurantdelivery

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.androidghanem.domain.utils.LocaleHelper
import com.androidghanem.oynxrestaurantdelivery.ui.navigation.AppNavigation
import com.androidghanem.oynxrestaurantdelivery.ui.theme.OynxRestaurantDeliveryTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private lateinit var appInstance: OnyxApplication

    override fun attachBaseContext(newBase: Context) {
        appInstance = newBase.applicationContext as OnyxApplication
        val languageCode = appInstance.preferencesManager.getLanguageCode()
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

        lifecycleScope.launch(Dispatchers.IO) {
            appInstance.preferencesManager.languageCode.collect { newLanguageCode ->
                withContext(Dispatchers.Main) {
                    LocaleHelper.setLocale(this@MainActivity, newLanguageCode)
                }
            }
        }
    }
}