package com.androidghanem.domain.model

enum class OrderStatus {
    NEW,
    DELIVERING,
    DELIVERED,
    RETURNED
}

data class Order(
    val id: String,
    val status: OrderStatus,
    val totalPrice: String,
    val date: String
)