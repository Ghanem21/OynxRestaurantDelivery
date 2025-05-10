package com.androidghanem.oynxrestaurantdelivery.features.login.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.androidghanem.oynxrestaurantdelivery.R
import com.androidghanem.oynxrestaurantdelivery.features.login.presentation.LoginViewModel

/**
 * A button for language selection that is optimized for performance with stable content
 */
@Composable
fun LanguageSelection(
    viewModel: LoginViewModel,
    isRtl: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
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
}