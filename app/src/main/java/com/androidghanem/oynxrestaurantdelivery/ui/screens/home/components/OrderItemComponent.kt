package com.androidghanem.oynxrestaurantdelivery.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 91.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Order ID and Status
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 19.dp, top = 4.dp, bottom = 24.dp)
            ) {
                Text(
                    text = "#${order.id}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )


                Text(
                    modifier = Modifier.padding(start = 20.dp),
                    text = stringResource(R.string.label_status),
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Text(
                    modifier = Modifier.padding(start = 20.dp),
                    text = getStatusDisplayText(order.status),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = getStatusColor(order.status)
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
                    .padding(vertical = 21.dp),
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
                    fontWeight = FontWeight.Bold
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
                    .padding(vertical = 21.dp),
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
                    fontWeight = FontWeight.Bold
                )
            }

            // Action button

            Column(
                modifier = Modifier
                    .width(44.dp)
                    .heightIn(min = 100.dp)
                    .background(getStatusColor(order.status)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.order_details_button),
                    color = Color.White,
                    fontSize = 8.sp,
                    lineHeight = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun getStatusDisplayText(status: OrderStatus): String {
    return when (status) {
        OrderStatus.NEW -> stringResource(R.string.tab_new)
        OrderStatus.DELIVERING -> "Delivering"
        OrderStatus.DELIVERED -> "Delivered"
        OrderStatus.RETURNED -> "Returned"
    }
}

@Composable
private fun getStatusColor(status: OrderStatus): Color {
    return when (status) {
        OrderStatus.NEW -> StatusGreen
        OrderStatus.DELIVERING -> PrimaryTeal
        OrderStatus.DELIVERED -> StatusGray
        OrderStatus.RETURNED -> StatusRed
    }
}