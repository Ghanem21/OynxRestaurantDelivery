package com.androidghanem.oynxrestaurantdelivery.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidghanem.oynxrestaurantdelivery.R
import kotlinx.coroutines.delay

enum class ToastType {
    ERROR,
    SUCCESS
}

@Composable
fun AppToast(
    message: String,
    type: ToastType = ToastType.ERROR,
    onDismiss: () -> Unit
) {
    val backgroundColor = when (type) {
        ToastType.ERROR -> Color(0xFFE53935)
        ToastType.SUCCESS -> Color(0xFF4CAF50)
    }

    val iconRes = when (type) {
        ToastType.ERROR -> R.drawable.ic_launcher_foreground // Replace with actual error icon
        ToastType.SUCCESS -> R.drawable.ic_launcher_foreground // Replace with actual success icon
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Text(
                        text = message,
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    // Auto dismiss after 3 seconds
    LaunchedEffect(message) {
        delay(3000)
        onDismiss()
    }
}