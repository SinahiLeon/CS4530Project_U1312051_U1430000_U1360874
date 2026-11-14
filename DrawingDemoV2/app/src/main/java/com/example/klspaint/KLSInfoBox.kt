package com.example.klspaint

import androidx.compose.ui.geometry.Rect

data class KLSInfoBox(
    val rect: Rect,
    val description: String?,
    val score: Float?
)