package com.androidghanem.oynxrestaurantdelivery.ui.screens.login

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.androidghanem.oynxrestaurantdelivery.R
import com.androidghanem.oynxrestaurantdelivery.ui.components.AppToast
import com.androidghanem.oynxrestaurantdelivery.ui.components.ToastType
import com.androidghanem.oynxrestaurantdelivery.ui.screens.login.components.LanguageDialog
import com.androidghanem.oynxrestaurantdelivery.ui.screens.login.components.LanguageSelection
import com.androidghanem.oynxrestaurantdelivery.ui.screens.login.components.LoginButton
import com.androidghanem.oynxrestaurantdelivery.ui.screens.login.components.LoginTextField
import com.androidghanem.oynxrestaurantdelivery.ui.theme.BackgroundGray
import com.androidghanem.oynxrestaurantdelivery.ui.theme.PrimaryTeal

/**
 * Login screen for user authentication
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val layoutDirection = LocalLayoutDirection.current
    val isRtl = layoutDirection == LayoutDirection.Rtl

    // Check if we're in landscape mode
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val scrollState = rememberScrollState()

    // Handle successful login navigation
    LaunchedEffect(uiState.isLoginSuccessful) {
        if (uiState.isLoginSuccessful) {
            onLoginSuccess()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
            .then(if (isLandscape) Modifier.verticalScroll(scrollState) else Modifier)
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.onxrestaurant_logo),
            contentDescription = "Onyx Logo",
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 36.dp, start = 26.dp)
                .width(170.dp)
                .height(75.dp),
            contentScale = ContentScale.FillBounds
        )

        // Top right quarter circle
        Image(
            painter = painterResource(id = R.drawable.log_in_quarter_circle),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .graphicsLayer {
                    scaleX = if (isRtl) -1f else 1f
                },
            contentScale = ContentScale.FillBounds,
        )

        // Language selection button
        LanguageSelection(
            viewModel = viewModel,
            isRtl = isRtl,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        )

        // Main content column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            // Welcome text
            Text(
                text = stringResource(R.string.welcome_back),
                fontSize = 29.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryTeal,
                modifier = Modifier.padding(top = 40.dp)
            )

            Text(
                text = stringResource(R.string.login_subtitle),
                fontSize = 12.sp,
                color = PrimaryTeal,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            // User ID field
            LoginTextField(
                value = uiState.userId,
                onValueChange = viewModel::onUserIdChange,
                label = stringResource(R.string.user_id),
                modifier = Modifier.padding(top = 16.dp)
            )

            // Password field
            LoginTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                label = stringResource(R.string.password),
                visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                modifier = Modifier.padding(top = 16.dp)
            )
            
            // Show/Hide password toggle
            Text(
                text = stringResource(R.string.show_more),
                color = PrimaryTeal,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.End)
                    .clickable { viewModel.togglePasswordVisibility() }
            )

            // Login button
            LoginButton(
                text = stringResource(R.string.login),
                onClick = viewModel::login,
                isLoading = uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Delivery illustration
            Image(
                painter = painterResource(id = R.drawable.log_in_delivery_car),
                contentDescription = "Delivery illustration",
                modifier = Modifier
                    .height(170.dp)
                    .width(195.dp)
                    .padding(bottom = 16.dp)
                    .graphicsLayer {
                        scaleX = if (isRtl) -1f else 1f
                    },
                contentScale = ContentScale.Fit
            )
        }

        // Language Dialog
        if (uiState.isLanguageDialogVisible) {
            LanguageDialog(
                languages = uiState.availableLanguages,
                onLanguageSelected = viewModel::selectAndApplyLanguage,
                onDismiss = viewModel::toggleLanguageDialog
            )
        }

        // Error toast
        if (uiState.errorMessage != null) {
            AppToast(
                message = uiState.errorMessage!!,
                type = ToastType.ERROR,
                title = uiState.errorMessageTitle,
                onDismiss = viewModel::clearError
            )
        }
    }
}