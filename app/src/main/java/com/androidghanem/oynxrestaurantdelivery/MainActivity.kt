package com.androidghanem.oynxrestaurantdelivery

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.androidghanem.data.session.SessionManager
import com.androidghanem.domain.utils.LocaleHelper
import com.androidghanem.oynxrestaurantdelivery.ui.navigation.AppNavigation
import com.androidghanem.oynxrestaurantdelivery.ui.theme.OynxRestaurantDeliveryTheme
import com.androidghanem.oynxrestaurantdelivery.ui.util.UserActivityTracker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private val appInstance: OnyxApplication
        get() = applicationContext as OnyxApplication
    
    @Inject
    lateinit var userActivityTracker: UserActivityTracker
    
    @Inject
    lateinit var sessionManager: SessionManager

    override fun attachBaseContext(newBase: Context) {
        val tempApp = newBase.applicationContext as OnyxApplication
        val languageCode = tempApp.preferencesManager.getLanguageCode()
        super.attachBaseContext(LocaleHelper.setLocale(newBase, languageCode))
    }
    
    override fun onResume() {
        super.onResume()
        if (sessionManager.isLoggedIn.value) {
            Log.d("SessionExpiration", "Resetting session timer on activity resume")
            appInstance.resetSessionTimer()
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d("SessionExpiration", "Activity stopped, app going to background")
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        userActivityTracker.onUserInteraction()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        userActivityTracker.initialize(this)
        
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

        Log.d("SessionExpiration", "Starting session monitoring")
        appInstance.resetSessionTimer()
    }
}