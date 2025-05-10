package com.androidghanem.oynxrestaurantdelivery.ui.screens.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidghanem.oynxrestaurantdelivery.R
import com.androidghanem.oynxrestaurantdelivery.ui.theme.HeaderRed
import com.androidghanem.oynxrestaurantdelivery.ui.theme.PrimaryTeal

@Composable
fun HomeTopBar(
    name: String,
    onLanguageClick: () -> Unit = {},
    isOfflineMode: Boolean = false
) {

    val layoutDirection = LocalLayoutDirection.current
    val isRtl = layoutDirection == LayoutDirection.Rtl

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(128.dp)
            .background(color = HeaderRed)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(128.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 27.dp, horizontal = 16.dp)
                    .weight(1f)
            ) {
                val nameParts = name.split(" ")
                val firstName = nameParts.firstOrNull() ?: ""
                val lastName = if (nameParts.size > 1) nameParts.last() else ""

                Text(
                    text = firstName,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = lastName,
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                
                // Offline mode indicator
                if (isOfflineMode) {
                    Text(
                        text = "OFFLINE MODE",
                        color = Color.Yellow,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Image(
                painter = painterResource(id = R.drawable.ic_circle),
                contentDescription = null,
                modifier = Modifier
                    .size(127.dp)
                    .graphicsLayer {
                        scaleX = if (isRtl) -1f else 1f
                    },
                contentScale = ContentScale.FillBounds,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.5f)
                .align(Alignment.TopEnd)
        ) {
            // Delivery person image
            Image(
                painter = painterResource(id = R.drawable.deliveryman),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 60.dp, top = 20.dp)
                    .graphicsLayer {
                        scaleX = if (isRtl) -1f else 1f
                    }
            )

            // Language icon
            IconButton(
                onClick = onLanguageClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_language),
                    contentDescription = null,
                    tint = PrimaryTeal,
                    modifier = Modifier
                        .size(24.dp)
                        .background(color = Color.White, shape = RoundedCornerShape(5.dp))
                        .padding(5.dp)
                )
            }
        }
    }

}

@Preview
@Composable
fun HomeTopBarPreview() {
    HomeTopBar(name = "John Doe")
}

@Preview
@Composable
fun HomeTopBarOfflinePreview() {
    HomeTopBar(name = "John Doe", isOfflineMode = true)
}