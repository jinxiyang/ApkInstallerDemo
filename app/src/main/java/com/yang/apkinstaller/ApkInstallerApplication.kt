package com.yang.apkinstaller

import android.app.Application
import androidx.room.Room
import com.yang.apkinstaller.database.AppDatabase

class ApkInstallerApplication : Application() {

    val db by lazy {
        Room.databaseBuilder(this, AppDatabase::class.java, DATABASE_NAME)
            .build()
    }

    companion object {
        val DATABASE_NAME = "ApkInstallerDb"
    }
}