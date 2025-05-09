package com.androidghanem.data.network.model.response

import com.androidghanem.domain.model.DeliveryStatusType

data class DeliveryStatusTypesWrapper(
    val DeliveryStatusTypes: List<DeliveryStatusTypeResponse>? = null
)

data class DeliveryStatusTypeResponse(
    val TYP_NO: String,
    val TYP_NM: String
) {
    fun toDomain(): DeliveryStatusType {
        return DeliveryStatusType(
            typeNumber = TYP_NO,
            typeName = TYP_NM
        )
    }
}