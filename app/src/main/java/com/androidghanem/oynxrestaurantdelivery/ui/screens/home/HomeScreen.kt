package com.androidghanem.oynxrestaurantdelivery.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.androidghanem.oynxrestaurantdelivery.R
import com.androidghanem.oynxrestaurantdelivery.ui.screens.home.components.EmptyOrdersState
import com.androidghanem.oynxrestaurantdelivery.ui.screens.home.components.HomeTopBar
import com.androidghanem.oynxrestaurantdelivery.ui.screens.home.components.OrderTabs
import com.androidghanem.oynxrestaurantdelivery.ui.screens.home.components.OrdersList
import com.androidghanem.oynxrestaurantdelivery.ui.screens.login.components.LanguageDialog
import com.androidghanem.oynxrestaurantdelivery.ui.theme.BackgroundGray
import com.androidghanem.oynxrestaurantdelivery.ui.theme.PrimaryTeal

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val orderTabState by homeViewModel.orderTabState.collectAsState()
    val orders by homeViewModel.orders.collectAsState()
    val isLoading by homeViewModel.isLoading.collectAsState()
    val uiState by homeViewModel.uiState.collectAsState()
    val driverName by homeViewModel.driverName.collectAsState()
    val isOfflineMode by homeViewModel.isOfflineMode.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    
    val offlineMessage = stringResource(R.string.offline_mode)
    
    LaunchedEffect(isOfflineMode) {
        if (isOfflineMode) {
            snackbarHostState.showSnackbar(
                message = offlineMessage,
                withDismissAction = true
            )
        }
    }

    Scaffold(
        containerColor = BackgroundGray,
        topBar = { 
            HomeTopBar(
                name = driverName,
                onLanguageClick = { homeViewModel.toggleLanguageDialog() },
                isOfflineMode = isOfflineMode
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundGray)
        ) {
            OrderTabs(
                selectedTab = orderTabState,
                onTabSelected = { homeViewModel.updateOrderTab(it) }
            )
            
            Box(modifier = Modifier.fillMaxSize()) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = PrimaryTeal
                    )
                } else if (orders.isEmpty()) {
                    EmptyOrdersState()
                } else {
                    OrdersList(orders = orders)
                }
            }
        }
        
        // Language Dialog
        if (uiState.isLanguageDialogVisible) {
            LanguageDialog(
                languages = uiState.availableLanguages,
                onLanguageSelected = { language ->
                    homeViewModel.selectLanguage(language)
                    homeViewModel.applyLanguageChange()
                },
                onDismiss = { homeViewModel.toggleLanguageDialog() }
            )
        }
    }
}