package com.androidghanem.oynxrestaurantdelivery.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        // In a real app, you might want to do initial data loading here
        // For now, we'll just rely on the UI-level timer
    }
    
    fun onSplashFinished() {
        _isLoading.value = false
    }
}