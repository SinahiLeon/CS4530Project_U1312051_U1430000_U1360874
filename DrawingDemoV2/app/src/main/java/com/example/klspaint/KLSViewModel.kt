package com.example.klspaint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset  // Represents an (x,y) position on the canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.io.ByteArrayOutputStream

class KLSViewModel(private val repository: KLSRepository) : ViewModel() {
    // strokes = holds all strokes in drawing. Canvas will redraw
    // itself as it observes strokes change

    private val _drawing = MutableStateFlow(KLSDrawing(byteArrayOf()))
    val drawing: Flow<Bitmap> = _drawing.map { BitmapFactory.decodeByteArray(it.imageBytes, 0,it.imageBytes.size)}

    val allDrawings: StateFlow<List<KLSDrawing?>> =
        repository.allDrawings.stateIn(
            scope = repository.scope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = listOf()
        )
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

    // Helper method for testing size of currentStrokePoints
    fun getPointCount(): Int {
        return currentStrokePoints.size
    }

    fun saveDrawing() {
        val stream = ByteArrayOutputStream()
        drawing.collectAsState().compress(Bitmap.CompressFormat.PNG, 90, stream)
        repository.addDrawing(KLSDrawing()))
    }

    fun deleteDrawing(drawing : KLSDrawing) {
        repository.deleteDrawing(drawing)
    }
}

object KLSViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            KLSViewModel(
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                        as KLSApplication).klsRepository
            )
        }
    }
}