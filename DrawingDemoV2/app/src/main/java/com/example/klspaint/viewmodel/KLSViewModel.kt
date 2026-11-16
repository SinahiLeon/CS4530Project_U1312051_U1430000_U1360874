package com.example.klspaint.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import com.example.klspaint.model.KLSDrawing
import com.example.klspaint.model.KLSInfoBox
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.klspaint.data.KLSRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json


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

    private var _infoBoxes = MutableStateFlow<List<KLSInfoBox>>(emptyList())
    val infoBoxes: StateFlow<List<KLSInfoBox>> = _infoBoxes.asStateFlow()
    private var currentDrawing: KLSDrawing = KLSDrawing("", "")
    var currentDrawingName by mutableStateOf("")
        private set

    var image by _root_ide_package_.androidx.compose.runtime.mutableStateOf<android.graphics.Bitmap?>(
        null
    )
        private set

    private var selectedPaint = _root_ide_package_.android.graphics.Paint().apply {
        isAntiAlias = true
        style = android.graphics.Paint.Style.FILL_AND_STROKE
        strokeCap = android.graphics.Paint.Cap.ROUND
        strokeJoin = android.graphics.Paint.Join.ROUND
        color = androidx.compose.ui.graphics.Color.Black.toArgb()
        strokeWidth = 8f
    }

    var selectedColor by _root_ide_package_.androidx.compose.runtime.mutableStateOf(androidx.compose.ui.graphics.Color.Black)
        private set

    var penSize by _root_ide_package_.androidx.compose.runtime.mutableStateOf(8f)
        private set

    var brushType by _root_ide_package_.androidx.compose.runtime.mutableStateOf(com.example.klspaint.BrushType.LINE)
        private set

    private var canvas: android.graphics.Canvas? = null
    var pExpanded by _root_ide_package_.androidx.compose.runtime.mutableStateOf(false)
    var cExpanded by _root_ide_package_.androidx.compose.runtime.mutableStateOf(false)
    var sExpanded by _root_ide_package_.androidx.compose.runtime.mutableStateOf(false)
    var vExpanded by _root_ide_package_.androidx.compose.runtime.mutableStateOf(false)


    var overlayVisible by _root_ide_package_.androidx.compose.runtime.mutableStateOf(false)

    private var lastOffset: androidx.compose.ui.geometry.Offset? = null // Offset represents 1 position using an X Y coordinate

    fun changeColor(color: androidx.compose.ui.graphics.Color) {
        selectedColor = color
        selectedPaint.color = color.toArgb()
    }

    fun changePenSize(size: Float) {
        penSize = size
        selectedPaint.strokeWidth = size
    }

    fun changeBrushType(type: com.example.klspaint.BrushType) {
        brushType = type
    }

    @androidx.annotation.RequiresApi(android.os.Build.VERSION_CODES.Q)
    fun createNewDrawing(width: Int = 1080, height: Int = 1920) {
        val bitmap = _root_ide_package_.androidx.core.graphics.createBitmap(width, height)
        val thisCanvas = _root_ide_package_.android.graphics.Canvas(bitmap)
        thisCanvas.drawColor(android.graphics.Color.WHITE)
        image = bitmap
        canvas = thisCanvas
        currentDrawing = _root_ide_package_.com.example.klspaint.model.KLSDrawing(genFilePath())
        currentDrawingName = ""
    }

    // When the cursor first touches the screen, this function receives the loc of that cursor
    fun startStroke(offset: androidx.compose.ui.geometry.Offset) {
        lastOffset = offset
        addPoint(offset)
    }

    // Receives the newest (x,y) position as the user's finger moves
    fun addPoint(offset: androidx.compose.ui.geometry.Offset) {
        val thisCanvas = canvas ?: return
        val half = penSize / 2
        when (brushType) {
            com.example.klspaint.BrushType.LINE -> {
                val temp = lastOffset
                if (temp != null) {
                    thisCanvas.drawLine(
                        temp.x, temp.y,
                        offset.x, offset.y,
                        selectedPaint
                    )
                }
            }

            com.example.klspaint.BrushType.CIRCLE -> {
                thisCanvas.drawCircle(offset.x, offset.y, half, selectedPaint)
            }

            com.example.klspaint.BrushType.RECTANGLE -> {
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
        val file = _root_ide_package_.java.io.File(
            appContext.filesDir,
            "img_${System.currentTimeMillis()}.png"
        )
        return file.absolutePath
    }

    fun saveDrawing(name: String) {
        val bitmap = image ?: return
        currentDrawing.name = name
        currentDrawingName = name
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val file = _root_ide_package_.java.io.File(currentDrawing.imagePath)
            _root_ide_package_.java.io.FileOutputStream(file).use {
                bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it)
            }

            repository.addDrawing(currentDrawing)
        }
    }

    fun loadDrawing(drawing: com.example.klspaint.model.KLSDrawing) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            currentDrawing = drawing
            currentDrawingName = drawing.name
            val file = _root_ide_package_.java.io.File(drawing.imagePath)
            if (file.exists()) {
                val bitmap = android.graphics.BitmapFactory
                    .decodeFile(file.absolutePath)
                    .copy(android.graphics.Bitmap.Config.ARGB_8888, true)
                bitmap.let {
                    _root_ide_package_.kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        image = it
                        canvas = _root_ide_package_.android.graphics.Canvas(it)
                    }
                }
            }
        }
    }

    fun deleteDrawing() {
        _root_ide_package_.java.io.File(currentDrawing.imagePath).delete()
        repository.deleteDrawing(currentDrawing)
    }

    fun importImage(uri: android.net.Uri) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val inputStream = appContext.contentResolver.openInputStream(uri)
            val decodedBitmap =
                android.graphics.BitmapFactory.decodeStream(inputStream)?.copy(android.graphics.Bitmap.Config.ARGB_8888, true)
            inputStream?.close()
            currentDrawing =
                _root_ide_package_.com.example.klspaint.model.KLSDrawing(genFilePath(), "")
            _root_ide_package_.kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                currentDrawingName = ""
            }

            decodedBitmap?.let { bitmap ->
                println("analyzing imported image...")
                _root_ide_package_.kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    image = bitmap
                    canvas = _root_ide_package_.android.graphics.Canvas(bitmap)
                    getAiAnalysis(bitmap.asImageBitmap())
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

    fun shareDrawing(drawing: com.example.klspaint.model.KLSDrawing? = null) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val targetDrawing = drawing ?: currentDrawing
            val file = if (targetDrawing.imagePath.isNotBlank()) {
                val existingFile = _root_ide_package_.java.io.File(targetDrawing.imagePath)
                // if a path and file exists, use it
                if (existingFile.exists()) {
                    existingFile
                // if a path but not a file exists, create a new file
                } else {
                    val bitmap = image ?: return@launch
                    existingFile.parentFile?.mkdirs()
                    // save to file (same as in saveDrawing)
                    _root_ide_package_.java.io.FileOutputStream(existingFile).use {
                        bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it)
                    }
                    existingFile
                }
            // if both a path and file do not exist, create a temporary path in the cache and add a file
            } else {
                val bitmap = image ?: return@launch
                val cacheFile = _root_ide_package_.java.io.File(
                    appContext.cacheDir,
                    "shared_drawing_${System.currentTimeMillis()}.png"
                )
                _root_ide_package_.java.io.FileOutputStream(cacheFile).use {
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it)
                }
                cacheFile
            }


            val uri: android.net.Uri = androidx.core.content.FileProvider.getUriForFile(
                appContext,
                "${appContext.packageName}.provider",
                file
            )

            // Pull up the sharing screen
            val intent = _root_ide_package_.android.content.Intent(android.content.Intent.ACTION_SEND)
                .apply {
                type = "image/png"
                putExtra(android.content.Intent.EXTRA_STREAM, uri)
                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = android.content.Intent.createChooser(intent, "Share Drawing").apply {
                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            appContext.startActivity(chooser)
        }
    }



    fun getAiAnalysis(image: androidx.compose.ui.graphics.ImageBitmap) {
        android.util.Log.d("VISION_TEST", "AI ANALYSIS FUNCTION CALLED")

        val bitmap = this.image ?: return
        viewModelScope.launch() {
            try {
                // From the documentation: BASE64_ENCODED_IMAGE: The base64
                // representation (ASCII string) of your binary image data.
                // This string should look similar to the following string:
                //
                //    /9j/4QAYRXhpZgAA...9tAVx/zDQDlGxn//2Q==
                //
                val byteArrayOutputStream = java.io.ByteArrayOutputStream()
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream)
                val imageBytes = byteArrayOutputStream.toByteArray()
                val base64Image = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT)

                // Build the request object with specific parameters and the image
                val requestObj = RequestObj(
                    requests = listOf(
                        Request(
                            image = Image(content = base64Image),
                            features = listOf(
                                Feature(maxResults = 10, type = "OBJECT_LOCALIZATION"), // can change the maxResults if needed
                            )
                        )
                    )
                )

                // Requirements specify the ViewModel calls Repo which communicates with Google API
                val response: ResponseObj = repository.analyzeImage(requestObj)

//                val width = bitmap.width.toFloat()
//                val height = bitmap.height.toFloat()
//                println("width: $width, height: $height")

                // hard-coded width and height instead.
                val boxes = parseVisionOutput(response, 1f, 1f)
                _infoBoxes.value = boxes
                overlayVisible = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun parseVisionOutput(responseObj: ResponseObj, width: Float, height: Float): List<com.example.klspaint.model.KLSInfoBox> {
        val boxes = mutableListOf<com.example.klspaint.model.KLSInfoBox>()
        try {
            responseObj.responses.forEach { response ->
                response.localizedObjectAnnotations?.forEach { obj ->
                    val vertices = obj.boundingPoly?.normalizedVertices

                    if (!vertices.isNullOrEmpty()) {
                        var minX = 1f
                        var minY = 1f
                        var maxX = 0f
                        var maxY = 0f

                        vertices.forEach { v ->
                            val x = v.x.coerceIn(0f, 1f)
                            val y = v.y.coerceIn(0f, 1f)
                            if (x < minX) minX = x
                            if (y < minY) minY = y
                            if (x > maxX) maxX = x
                            if (y > maxY) maxY = y
                        }

                        val left = minX * width
                        val top = minY * height
                        val right = maxX * width
                        val bottom = maxY * height

                        boxes.add(
                            _root_ide_package_.com.example.klspaint.model.KLSInfoBox(
                                _root_ide_package_.androidx.compose.ui.geometry.Rect(
                                    left,
                                    top,
                                    right,
                                    bottom
                                ),
                                obj.name,
                                obj.score
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return boxes
    }
}

// From HttpRequest Demo
// From HttpRequest Demo
object ApiClient {
    val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }
}

// Google Vision Request JSON body structure and the objects
// {
//  "requests": [
//    {
//      "image": {
//        "content": "BASE64_ENCODED_IMAGE"
//      },
//      "features": [
//        {
//          "maxResults": RESULTS_INT,
//          "type": "OBJECT_LOCALIZATION"
//        },
//      ]
//    }
//  ]
//}
@kotlinx.serialization.Serializable
data class RequestObj(
    val requests: List<Request>
)

@kotlinx.serialization.Serializable
data class Request(
    val image: Image,
    val features: List<Feature>
)

@kotlinx.serialization.Serializable
data class Image(
    val content: String
)


@kotlinx.serialization.Serializable
data class Feature(
    val maxResults: Int,
    val type: String
)


// Google Vision Response object
// "responses": [
//    {
//      "localizedObjectAnnotations": [
//        {
//          "mid": "/m/01bqk0",
//          "name": "Bicycle wheel",
//          "score": 0.89648587,
//          "boundingPoly": {
//            "normalizedVertices": [
//              {
//                "x": 0.32076266,
//                "y": 0.78941387
//              },
//              {
//                "x": 0.43812272,
//                "y": 0.78941387
//              },
//              {
//                "x": 0.43812272,
//                "y": 0.97331065
//              },
//              {
//                "x": 0.32076266,
//                "y": 0.97331065
//              }
//            ]
//          }
//        }
//       ]
//     }
//   ]
// }
@kotlinx.serialization.Serializable
data class ResponseObj(
    val responses: List<Response> = emptyList()
)

@kotlinx.serialization.Serializable
data class Response(
    val localizedObjectAnnotations: List<LocalizedObjectAnnotation>? = null,
)

@kotlinx.serialization.Serializable
data class LocalizedObjectAnnotation(
    val name: String? = null,
    val score: Float = 0f,
    val boundingPoly: BoundingPoly? = null
)

@kotlinx.serialization.Serializable
data class BoundingPoly(
    val normalizedVertices: List<NormalizedVertex>? = null
)

@kotlinx.serialization.Serializable
data class NormalizedVertex(
    val x: Float = 0f,
    val y: Float = 0f
)

object KLSViewModelProvider {
    val Factory = _root_ide_package_.androidx.lifecycle.viewmodel.viewModelFactory {
        initializer {
            val app =
                this[androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as com.example.klspaint.KLSApplication
            KLSViewModel(app.klsRepository, app)
        }
    }
}