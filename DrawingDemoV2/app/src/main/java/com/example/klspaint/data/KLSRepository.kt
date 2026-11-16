package com.example.klspaint.data

import android.util.Log
import com.example.klspaint.BuildConfig
import com.example.klspaint.model.KLSDrawing
import com.example.klspaint.viewmodel.ApiClient
import com.example.klspaint.viewmodel.RequestObj
import com.example.klspaint.viewmodel.ResponseObj
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
            Log.e(
                "VISION_TEST",
                "Vision API error ${response.status}: ${response.bodyAsText()}"
            )
            error("Vision API error: ${response.status}")
        }
        return response.body()
    }




}