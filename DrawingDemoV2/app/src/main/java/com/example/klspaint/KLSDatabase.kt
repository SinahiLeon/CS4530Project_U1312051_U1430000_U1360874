package com.example.klspaint

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Database(entities = [KLSDrawing::class], version = 1, exportSchema = false)
abstract class KLSDatabase : RoomDatabase() {
    abstract fun klsDao(): KLSDAO
}

@Dao
interface KLSDAO {
    @Insert
    suspend fun addDrawing(data: KLSDrawing)

    @Delete
    suspend fun deleteDrawing(data: KLSDrawing)

    @Query("SELECT * from KLSDrawing")
    fun allDrawings() : Flow<List<KLSDrawing>>

    @Query("SELECT * from KLSDrawing")
    fun getDrawing(data: KLSDrawing) : Flow<KLSDrawing>
}