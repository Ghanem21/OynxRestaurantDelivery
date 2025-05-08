package com.androidghanem.oynxrestaurantdelivery.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.androidghanem.oynxrestaurantdelivery.R
import com.androidghanem.oynxrestaurantdelivery.ui.theme.LightBlue
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    LaunchedEffect(key1 = true) {
        delay(3000)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBlue),
        contentAlignment = Alignment.Center
    ) {
        val layoutDirection = LocalLayoutDirection.current
        val isRtl = layoutDirection == LayoutDirection.Rtl

        Image(
            painter = painterResource(id = R.drawable.splash_screen_bg),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .graphicsLayer {
                    scaleX = if (isRtl) -1f else 1f
                }
        )

        Image(
            painter = painterResource(id = R.drawable.splash_screen_logo),
            contentDescription = stringResource(R.string.splash_logo_desc),
            modifier = Modifier
                .width(275.dp)
                .height(115.dp)
                .align(Alignment.Center)
        )

        Image(
            painter = painterResource(id = R.drawable.splash_screen_delivery),
            contentDescription = stringResource(R.string.splash_delivery_desc),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 50.dp)
                .size(275.dp)
                .graphicsLayer {
                    scaleX = if (isRtl) -1f else 1f
                }
        )
    }
}