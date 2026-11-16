package com.example.klspaint

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.client.statement.bodyAsText

class KLSRepository(val scope: CoroutineScope, private val dao: KLSDAO) {
    val allDrawings = dao.allDrawings()

    fun addDrawing(klsDrawing: KLSDrawing){
        scope.launch {
            dao.addDrawing(klsDrawing)
        }
    }

    fun deleteDrawing(klsDrawing: KLSDrawing){
        scope.launch {
            dao.deleteDrawing(klsDrawing)
        }
    }

    // Communicating with Google cloud vision
    suspend fun analyzeImage(requestObj: RequestObj): ResponseObj {
        val apiKey = BuildConfig.VISION_API_KEY

        val response = ApiClient.client.post(
            "https://vision.googleapis.com/v1/images:annotate?key=$apiKey"
        ){
            contentType(ContentType.Application.Json)
            setBody(requestObj)
        }
        if (response.status.value !in 200..299) {
            android.util.Log.e(
                "VISION_TEST",
                "Vision API error ${response.status}: ${response.bodyAsText()}"
            )
            error("Vision API error: ${response.status}")
        }
        return response.body()
    }




}