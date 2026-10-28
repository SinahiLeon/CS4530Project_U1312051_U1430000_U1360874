package com.example.klspaint

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class KLSDrawing(val imageBytes: ByteArray){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as KLSDrawing

        if (id != other.id) return false
        if (!imageBytes.contentEquals(other.imageBytes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + imageBytes.contentHashCode()
        return result
    }
}