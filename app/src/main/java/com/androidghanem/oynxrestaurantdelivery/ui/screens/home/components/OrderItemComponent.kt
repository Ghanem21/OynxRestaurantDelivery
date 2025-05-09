package com.androidghanem.oynxrestaurantdelivery.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidghanem.domain.model.Order
import com.androidghanem.domain.model.OrderStatus
import com.androidghanem.oynxrestaurantdelivery.R
import com.androidghanem.oynxrestaurantdelivery.ui.theme.PrimaryTeal
import com.androidghanem.oynxrestaurantdelivery.ui.theme.StatusGray
import com.androidghanem.oynxrestaurantdelivery.ui.theme.StatusGreen
import com.androidghanem.oynxrestaurantdelivery.ui.theme.StatusRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderItem(order: Order) {
    val layoutDirection = LocalLayoutDirection.current
    val isRtl = layoutDirection == LayoutDirection.Rtl

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(125.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.fillMaxHeight()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {

                Text(
                    text = "#${order.id}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Order ID and Status
                    Column(
                        modifier = Modifier
                            .weight(0.65f)
                            .padding(start = 8.dp, top = 4.dp, bottom = 8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.label_status),
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = getStatusDisplayText(order.status),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = getStatusColor(order.status),
                            textAlign = TextAlign.Center
                        )
                    }

                    // Vertical divider
                    VerticalDivider(
                        modifier = Modifier
                            .width(1.dp)
                            .height(50.dp),
                        color = Color.LightGray
                    )

                    // Total Price
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.label_total_price),
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = order.totalPrice,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Vertical divider
                    VerticalDivider(
                        modifier = Modifier
                            .width(1.dp)
                            .height(50.dp),
                        color = Color.LightGray
                    )

                    // Date
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 12.dp,bottom = 12.dp, start = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.label_date),
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = order.date,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }

            }
            Column(
                modifier = Modifier
                    .fillMaxHeight(1f)
                    .width(44.dp)
                    .background(getStatusColor(order.status)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = getStatusShortName(order.status),
                    color = Color.White,
                    fontSize = 10.sp,
                    lineHeight = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(14.dp)
                        .graphicsLayer {
                            scaleX = if (isRtl) -1f else 1f
                        }
                )
            }

        }
    }
}

@Composable
private fun getStatusDisplayText(status: OrderStatus): String {
    return when (status) {
        OrderStatus.NEW -> stringResource(R.string.status_new)
        OrderStatus.DELIVERING -> stringResource(R.string.status_delivering)
        OrderStatus.DELIVERED -> stringResource(R.string.status_delivered)
        OrderStatus.RETURNED -> stringResource(R.string.status_returned)
        OrderStatus.PARTIAL_RETURN -> stringResource(R.string.status_partial_return)
    }
}

@Composable
private fun getStatusColor(status: OrderStatus): Color {
    return when (status) {
        OrderStatus.NEW -> StatusGreen
        OrderStatus.DELIVERING -> PrimaryTeal
        OrderStatus.DELIVERED -> StatusGray
        OrderStatus.RETURNED -> StatusRed
        OrderStatus.PARTIAL_RETURN -> StatusGray
    }
}

@Composable
private fun getStatusShortName(status: OrderStatus): String {
    return when (status) {
        OrderStatus.NEW -> stringResource(R.string.status_short_new)
        OrderStatus.DELIVERING -> stringResource(R.string.status_short_delivering)
        OrderStatus.DELIVERED -> stringResource(R.string.status_short_delivered)
        OrderStatus.RETURNED -> stringResource(R.string.status_short_returned)
        OrderStatus.PARTIAL_RETURN -> stringResource(R.string.status_short_partial_return)
    }
}