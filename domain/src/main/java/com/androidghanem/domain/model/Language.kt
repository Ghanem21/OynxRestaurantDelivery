package com.androidghanem.domain.model

data class Language(
    val code: String,
    val localizedName: String,
    val isSelected: Boolean = false
)