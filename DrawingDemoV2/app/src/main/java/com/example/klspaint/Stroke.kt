package com.example.klspaint

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class Stroke(
    val points: List<Offset>,
    val color: Color,
    val size: Float,
    val brushType: BrushType
)