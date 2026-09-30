package com.example.drawingdemov2
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

class DrawingViewModel : ViewModel() {
    // strokes = holds all strokes in drawing. Canvas will redraw
    // itself as it observes strokes change
    var strokes by mutableStateOf(listOf<Stroke>())
        private set

    // set default pen color to black
    var selectedColor by mutableStateOf(Color.Black)
        private set

    fun changeColor(color: Color) {
        selectedColor = color
    }
}