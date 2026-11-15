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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
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
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import kotlin.apply
import kotlinx.serialization.Serializable
//import com.google.ai.client.generativeai.GenerativeModel

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

    var overlayVisible by mutableStateOf(false)

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
        currentDrawingName = ""
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

    fun deleteDrawing() {
        File(currentDrawing.imagePath).delete()
        repository.deleteDrawing(currentDrawing)
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
                println("analyzing imported image...")
                withContext(Dispatchers.Main) {
                    image = bitmap
                    canvas = Canvas(bitmap)
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



    fun getAiAnalysis(image: ImageBitmap) {
        val bitmap = this.image ?: return
        viewModelScope.launch() {
            try {
                // From the documentation: BASE64_ENCODED_IMAGE: The base64 representation (ASCII string) of your binary image data. This string should look similar to the following string:
                //
                //    /9j/4QAYRXhpZgAA...9tAVx/zDQDlGxn//2Q==
                //
                val byteArrayOutputStream = java.io.ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream)
                val imageBytes = byteArrayOutputStream.toByteArray()
                val base64Image = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT)

                val requestObj = RequestObj(
                    requests = listOf(
                        Request(
                            image = Image(content = base64Image),
                            features = listOf(
                                Feature(maxResults = 10, type = "OBJECT_LOCALIZATION"),
                            )
                        )
                    )
                )

                val apiKey = BuildConfig.VISION_API_KEY

                // Make Ktor request
                val httpResponse = ApiClient.client.post("https://vision.googleapis.com/v1/images:annotate?key=$apiKey") {
                    contentType(ContentType.Application.Json)
                    setBody(requestObj)
                }

                android.util.Log.d("VisionAPI", "Response status: ${httpResponse.status}")
                val response: ResponseObj = httpResponse.body()
                android.util.Log.d("VisionAPI", "Parsed ResponseObj: $response")

//                val width = bitmap.width.toFloat()
//                val height = bitmap.height.toFloat()
//                println("width: $width, height: $height")

                // hardc-oded width and height instead.
                val boxes = parseVisionOutput(response, 1080f, 1760f)
                _infoBoxes.value = boxes
                overlayVisible = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun parseVisionOutput(responseObj: ResponseObj, width: Float, height: Float): List<KLSInfoBox> {
        val boxes = mutableListOf<KLSInfoBox>()
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

                        boxes.add(KLSInfoBox(Rect(left, top, right, bottom), obj.name, obj.score))
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
object ApiClient{
    val client = HttpClient(Android){
        install(ContentNegotiation){
            json(Json {
                isLenient = true
                ignoreUnknownKeys =true

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
@Serializable
data class RequestObj(
    val requests: List<Request>
)

@Serializable
data class Request(
    val image: Image,
    val features: List<Feature>
)

@Serializable
data class Image(
    val content: String
)


@Serializable
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
@Serializable
data class ResponseObj(
    val responses: List<Response> = emptyList()
)

@Serializable
data class Response(
    val localizedObjectAnnotations: List<LocalizedObjectAnnotation>? = null,
)

@Serializable
data class LocalizedObjectAnnotation(
    val name: String? = null,
    val score: Float = 0f,
    val boundingPoly: BoundingPoly? = null
)
@Serializable
data class BoundingPoly(
    val normalizedVertices: List<NormalizedVertex>? = null
)

@Serializable
data class NormalizedVertex(
    val x: Float = 0f,
    val y: Float = 0f
)

object KLSViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as KLSApplication
            KLSViewModel( app.klsRepository, app)
        }
    }
}