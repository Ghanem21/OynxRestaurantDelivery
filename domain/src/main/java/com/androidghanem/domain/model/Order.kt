package com.androidghanem.domain.model

enum class OrderStatus {
    NEW,
    DELIVERING, 
    DELIVERED,
    PARTIAL_RETURN,
    RETURNED;
    
    fun getDisplayText(isArabic: Boolean = false): String {
        return when (this) {
            NEW -> if (isArabic) "جديد" else "New"
            DELIVERING -> if (isArabic) "جاري التوصيل" else "Delivering"
            DELIVERED -> if (isArabic) "تم التوصيل" else "Delivered"
            PARTIAL_RETURN -> if (isArabic) "إرجاع جزئي" else "Partial Return"
            RETURNED -> if (isArabic) "تم الإرجاع" else "Returned"
        }
    }
}

data class Order(
    val id: String,
    val status: OrderStatus,
    val totalPrice: String,
    val date: String
)