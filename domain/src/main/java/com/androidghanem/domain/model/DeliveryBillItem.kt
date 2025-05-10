package com.androidghanem.domain.model

data class DeliveryBillItem(
    val billSerial: String,
    val billNumber: String,
    val billDate: String,
    val customerName: String,
    val totalAmount: Double,
    val statusFlag: String,
    val statusDescription: String,
    val region: String = "",
    val address: String = "",
    val mobileNumber: String = "",
    val deliveryAmount: String = "0"
) {
    fun toOrder(): Order {
        val status = when (statusFlag) {
            "0" -> OrderStatus.NEW
            "1" -> OrderStatus.DELIVERED
            "2" -> OrderStatus.PARTIAL_RETURN
            "3" -> OrderStatus.RETURNED
            else -> OrderStatus.NEW
        }
        
        // Format total price with currency and proper decimal formatting
        val formattedPrice = try {
            // Format to two decimal places and add currency
            val rounded = "%.2f".format(totalAmount)
            
            // Add thousand separators
            val parts = rounded.split(".")
            val intPart = parts[0].toLong()
            val formattedInt = String.format("%,d", intPart)
            
            if (parts.size > 1) {
                "$formattedInt.${parts[1]} LE"
            } else {
                "$formattedInt LE"
            }
        } catch (_: Exception) {
            "$totalAmount LE"
        }
        
        return Order(
            id = billSerial,
            status = status,
            totalPrice = formattedPrice,
            date = billDate
        )
    }
}