package com.example.drawingdemov2
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset  // Represents an (x,y) position on the canvas

class DrawingViewModel : ViewModel() {
    // strokes = holds all strokes in drawing. Canvas will redraw
    // itself as it observes strokes change
    var strokes by mutableStateOf(listOf<Stroke>())
        private set

    // set default pen color to black
    var selectedColor by mutableStateOf(Color.Black)
        private set

    var penSize by mutableStateOf(8f)
        private set

    var brushType by mutableStateOf(BrushType.LINE)
        private set

    var pExpanded by mutableStateOf( false )
    var cExpanded by mutableStateOf( false )
    var sExpanded by mutableStateOf( false )

    private var currentStrokePoints = listOf<Offset>()  // Offset represents 1 position using an X Y coordinate

    fun changeColor(color: Color) {
        selectedColor = color
    }

    fun changePenSize(size: Float) {
        penSize = size
    }

    fun changeBrushType(type: BrushType) {
        brushType = type
    }

    // When the cursor first touches the screen, this function receives the loc of that cursor
    fun startStroke(offset: Offset) {
        currentStrokePoints = listOf(offset)    // begin current stroke

        val newStroke = Stroke(     // creates a stroke object
            points = currentStrokePoints,   // where it was drawn
            color = selectedColor,
            size = penSize,
            brushType = brushType       // what shape
        )
        strokes = strokes + newStroke   // takes all existing strokes and adds this new one
        }

    // Receives the newest (x,y) position as the user's finger moves
    fun addPoint(offset: Offset) {
        currentStrokePoints = currentStrokePoints + offset  // Adds newest finger position to current stroke's list

        val updatedStroke = strokes.last().copy(    // gets most recently created Stroke and creates copy of it
            points = currentStrokePoints    // Points change as you are drawing, so we update it to the last saved points
        )
        strokes = strokes.dropLast(1) + updatedStroke
    }

    fun endStroke() {
        currentStrokePoints = emptyList()  // actively drawing nothing
    }

}