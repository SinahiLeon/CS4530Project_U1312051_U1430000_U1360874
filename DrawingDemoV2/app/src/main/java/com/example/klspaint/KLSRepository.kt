package com.example.klspaint

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
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
}