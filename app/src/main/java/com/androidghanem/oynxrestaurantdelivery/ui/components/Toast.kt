package com.androidghanem.oynxrestaurantdelivery.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidghanem.oynxrestaurantdelivery.R
import kotlinx.coroutines.delay

enum class ToastType {
    ERROR,
    SUCCESS,
    WARNING,
    INFO
}

@Composable
fun AppToast(
    message: String,
    type: ToastType = ToastType.ERROR,
    title: String? = null,
    onDismiss: () -> Unit
) {
    val backgroundColor = when (type) {
        ToastType.ERROR -> Color(0xFFE53935)
        ToastType.SUCCESS -> Color(0xFF4CAF50)
        ToastType.WARNING -> Color(0xFFFFA726)
        ToastType.INFO -> Color(0xFF2196F3)
    }

    when (type) {
        ToastType.ERROR -> R.drawable.ic_error
        ToastType.SUCCESS -> R.drawable.ic_success
        ToastType.WARNING -> R.drawable.ic_warning
        ToastType.INFO -> R.drawable.ic_info
    }

    val fallbackIcon = rememberSaveable { R.drawable.ic_launcher_foreground }
    
    val displayTitle = title ?: when (type) {
        ToastType.ERROR -> "Error"
        ToastType.SUCCESS -> "Success"
        ToastType.WARNING -> "Warning"
        ToastType.INFO -> "Information"
    }
    
    val iconContentDescription = when (type) {
        ToastType.ERROR -> "Error"
        ToastType.SUCCESS -> "Success"
        ToastType.WARNING -> "Warning"
        ToastType.INFO -> "Information"
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
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(0.85f)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top // Align to top for multi-line messages
                ) {
                    Icon(
                        painter = painterResource(id = fallbackIcon),
                        contentDescription = iconContentDescription,
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 12.dp)
                    )
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = displayTitle,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = message,
                            color = Color.White,
                            fontSize = 16.sp,
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(message) {
        delay(3000)
        onDismiss()
    }
}