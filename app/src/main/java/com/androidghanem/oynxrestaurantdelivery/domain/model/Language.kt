package com.androidghanem.oynxrestaurantdelivery.domain.model

data class Language(
    val code: String,
    val name: String,
    val localizedName: String,
    val isSelected: Boolean = false
)