package com.yang.apkinstaller.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yang.apkinstaller.database.bean.DownloadFileRecord
import com.yang.apkinstaller.database.dao.DownloadFileRecordDao

@Database(entities = [DownloadFileRecord::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun downloadFileRecordDao(): DownloadFileRecordDao
}