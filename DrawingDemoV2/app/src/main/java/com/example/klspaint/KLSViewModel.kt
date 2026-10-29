package com.example.klspaint

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import androidx.core.graphics.createBitmap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.apply

class KLSViewModel(
    private val repository: KLSRepository,
    private val appContext: Context
) : ViewModel() {
    // strokes = holds all strokes in drawing. Canvas will redraw
    // itself as it observes strokes change

    val allDrawings: StateFlow<List<KLSDrawing?>> =
        repository.allDrawings.stateIn(
            scope = repository.scope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = listOf()
        )

    private var currentDrawing: KLSDrawing = KLSDrawing("", "")
    var currentDrawingName by mutableStateOf("")
        private set

    var image by mutableStateOf<Bitmap?>(null)
        private set

    private var selectedPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL_AND_STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        color = Color.Black.toArgb()
        strokeWidth = 8f
    }

    var selectedColor by mutableStateOf(Color.Black)
        private set

    var penSize by mutableStateOf(8f)
        private set

    var brushType by mutableStateOf(BrushType.LINE)
        private set

    private var canvas: Canvas? = null
    var pExpanded by mutableStateOf(false)
    var cExpanded by mutableStateOf(false)
    var sExpanded by mutableStateOf(false)

    private var lastOffset: Offset? = null // Offset represents 1 position using an X Y coordinate

    fun changeColor(color: Color) {
        selectedColor = color
        selectedPaint.color = color.toArgb()
    }

    fun changePenSize(size: Float) {
        penSize = size
        selectedPaint.strokeWidth = size
    }

    fun changeBrushType(type: BrushType) {
        brushType = type
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun createNewDrawing(width: Int = 1080, height: Int = 1920) {
        val bitmap = createBitmap(width, height)
        val thisCanvas = Canvas(bitmap)
        thisCanvas.drawColor(android.graphics.Color.WHITE)
        image = bitmap
        canvas = thisCanvas
        currentDrawing = KLSDrawing(genFilePath())
    }

    // When the cursor first touches the screen, this function receives the loc of that cursor
    fun startStroke(offset: Offset) {
        lastOffset = offset
        addPoint(offset)
    }

    // Receives the newest (x,y) position as the user's finger moves
    fun addPoint(offset: Offset) {
        val thisCanvas = canvas ?: return
        val half = penSize / 2
        when (brushType) {
            BrushType.LINE -> {
                val temp = lastOffset
                if (temp != null) {
                    thisCanvas.drawLine(
                        temp.x, temp.y,
                        offset.x, offset.y,
                        selectedPaint
                    )
                }
            }

            BrushType.CIRCLE -> {
                thisCanvas.drawCircle(offset.x, offset.y, half, selectedPaint)
            }

            BrushType.RECTANGLE -> {
                thisCanvas.drawRect(
                    offset.x - half, offset.y - half,
                    offset.x + half, offset.y + half, selectedPaint
                )
            }
        }
        lastOffset = offset     //connect each segment to consecutive point
    }

    fun endStroke() {
        lastOffset = null
    }

    private fun genFilePath(): String {
        val file = File(appContext.filesDir, "img_${System.currentTimeMillis()}.png")
        return file.absolutePath
    }

    fun saveDrawing(name: String) {
        val bitmap = image ?: return
        currentDrawing.name = name
        currentDrawingName = name
        viewModelScope.launch(Dispatchers.IO) {
            val file = File(currentDrawing.imagePath)
            FileOutputStream(file).use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }

            repository.addDrawing(currentDrawing)
        }
    }

    fun loadDrawing(drawing: KLSDrawing) {
        viewModelScope.launch(Dispatchers.IO) {
            currentDrawing = drawing
            currentDrawingName = drawing.name
            val file = File(drawing.imagePath)
            if (file.exists()) {
                val bitmap = BitmapFactory
                    .decodeFile(file.absolutePath)
                    .copy(Bitmap.Config.ARGB_8888, true)
                bitmap.let {
                    withContext(Dispatchers.Main) {
                        image = it
                        canvas = Canvas(it)
                    }
                }
            }
        }
    }

    fun deleteDrawing(drawing: KLSDrawing) {
        File(drawing.imagePath).delete()
        repository.deleteDrawing(drawing)
    }

    fun importImage(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val inputStream = appContext.contentResolver.openInputStream(uri)
            val decodedBitmap =
                BitmapFactory.decodeStream(inputStream)?.copy(Bitmap.Config.ARGB_8888, true)
            inputStream?.close()
            currentDrawing = KLSDrawing(genFilePath(), "")
            withContext(Dispatchers.Main) {
                currentDrawingName = ""
            }

            decodedBitmap?.let { bitmap ->
                withContext(Dispatchers.Main) {
                    image = bitmap
                    canvas = Canvas(bitmap)
                }
            }
        }
    }

//    fun exportImage(fileName: String) {
//        val bitmap = image ?: return
//        viewModelScope.launch(Dispatchers.IO) {
//            val content = ContentValues().apply {
//                put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.png")
//                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
//                put(
//                    MediaStore.MediaColumns.RELATIVE_PATH,
//                    Environment.DIRECTORY_PICTURES + "/KLSPaint"
//                )
//            }
//
//            val imageUri = appContext.contentResolver.insert(
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                content
//            )
//            imageUri?.let {
//                appContext.contentResolver.openOutputStream(it).use {
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it!!)
//                }
//            }
//        }
//    }

    fun shareDrawing(drawing: KLSDrawing? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val targetDrawing = drawing ?: currentDrawing
            val file = if (targetDrawing.imagePath.isNotBlank()) {
                val existingFile = File(targetDrawing.imagePath)
                // if a path and file exists, use it
                if (existingFile.exists()) {
                    existingFile
                // if a path but not a file exists, create a new file
                } else {
                    val bitmap = image ?: return@launch
                    existingFile.parentFile?.mkdirs()
                    // save to file (same as in saveDrawing)
                    FileOutputStream(existingFile).use {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                    }
                    existingFile
                }
            // if both a path and file do not exist, create a temporary path in the cache and add a file
            } else {
                val bitmap = image ?: return@launch
                val cacheFile = File(appContext.cacheDir, "shared_drawing_${System.currentTimeMillis()}.png")
                FileOutputStream(cacheFile).use {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
                cacheFile
            }


            val uri: Uri = FileProvider.getUriForFile(
                appContext,
                "${appContext.packageName}.provider",
                file
            )

            // Pull up the sharing screen
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(intent, "Share Drawing").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            appContext.startActivity(chooser)
        }
    }
}



object KLSViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as KLSApplication
            KLSViewModel( app.klsRepository, app)
        }
    }
}