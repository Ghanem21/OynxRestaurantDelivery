package com.androidghanem.oynxrestaurantdelivery.ui.screens.login

import android.app.Application
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.androidghanem.oynxrestaurantdelivery.R
import com.androidghanem.oynxrestaurantdelivery.ui.components.AppToast
import com.androidghanem.oynxrestaurantdelivery.ui.components.ToastType
import com.androidghanem.oynxrestaurantdelivery.ui.screens.login.components.LanguageDialog
import com.androidghanem.oynxrestaurantdelivery.ui.screens.login.components.LoginButton
import com.androidghanem.oynxrestaurantdelivery.ui.screens.login.components.LoginTextField
import com.androidghanem.oynxrestaurantdelivery.ui.theme.BackgroundGray
import com.androidghanem.oynxrestaurantdelivery.ui.theme.PrimaryTeal

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    viewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(
            application = LocalContext.current.applicationContext as Application
        )
    ),
) {
    val uiState by viewModel.uiState.collectAsState()

    val layoutDirection = LocalLayoutDirection.current
    val isRtl = layoutDirection == LayoutDirection.Rtl

    LaunchedEffect(uiState.isLoginSuccessful) {
        if (uiState.isLoginSuccessful) {
            onLoginSuccess()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
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
                .size(127.dp)
                .graphicsLayer {
                    scaleX = if (isRtl) -1f else 1f
                },
            contentScale = ContentScale.FillBounds,
        )

        // Language icon
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(18.dp)
                .size(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .clickable { viewModel.toggleLanguageDialog() }
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_language),
                contentDescription = "Change language",
                modifier = Modifier
                    .size(27.dp)
                    .graphicsLayer {
                        scaleX = if (isRtl) -1f else 1f
                    }
            )
        }

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

            Text(
                text = stringResource(R.string.show_more),
                color = PrimaryTeal,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.End)
            )


            // Login button
            LoginButton(
                text = stringResource(R.string.login),
                onClick = {
                    viewModel.login()
                },
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
                    .fillMaxWidth()
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
                onLanguageSelected = { language ->
                    viewModel.selectLanguage(language)
                    viewModel.applyLanguageChange()
                },
                onDismiss = { viewModel.toggleLanguageDialog() }
            )
        }

        if (uiState.errorMessage != null) {
            AppToast(
                message = uiState.errorMessage!!,
                type = ToastType.ERROR,
                onDismiss = { viewModel.clearError() }
            )
        }
    }
}