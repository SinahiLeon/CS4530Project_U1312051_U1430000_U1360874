package com.example.klspaint.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class KLSDrawing(
    val imagePath: String,
    var name: String = ""
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}