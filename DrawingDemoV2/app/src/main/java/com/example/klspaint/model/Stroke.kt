package com.example.klspaint.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.klspaint.ui.BrushType

data class Stroke(
    val points: List<Offset>,
    val color: Color,
    val size: Float,
    val brushType: BrushType
)