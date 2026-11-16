package com.example.klspaint

import android.app.Application
import androidx.room.Room
import com.example.klspaint.data.KLSDatabase
import com.example.klspaint.data.KLSRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlin.getValue

class KLSApplication : Application() {
    val scope = CoroutineScope(SupervisorJob())

    val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            KLSDatabase::class.java,
            "kls_database"
        ).fallbackToDestructiveMigration(true).build()
    }

    val klsRepository by lazy { KLSRepository(scope, db.klsDao()) }
}