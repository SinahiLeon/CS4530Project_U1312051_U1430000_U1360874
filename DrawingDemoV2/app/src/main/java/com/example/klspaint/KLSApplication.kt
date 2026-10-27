package com.example.klspaint

import android.app.Application
import androidx.room.Room
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
        ).build()
    }

    val klsRepository by lazy { KLSRepository(scope, db.klsDao()) }
}