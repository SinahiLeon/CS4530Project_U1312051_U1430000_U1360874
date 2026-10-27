package com.example.klspaint

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class KLSDrawing(val strokes : List<Stroke>){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}