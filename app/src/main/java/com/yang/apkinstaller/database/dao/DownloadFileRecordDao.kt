package com.yang.apkinstaller.database.dao

import androidx.room.*
import com.yang.apkinstaller.database.bean.DownloadFileRecord

@Dao
interface DownloadFileRecordDao {

    @Query("SELECT * FROM DownloadFileRecord")
    fun getAll(): List<DownloadFileRecord>

    @Query("SELECT * FROM DownloadFileRecord WHERE id IN (:ids)")
    fun loadAllByIds(ids: IntArray): List<DownloadFileRecord>

    @Query("SELECT * FROM DownloadFileRecord WHERE fileName is :fileName")
    fun findByFileName(fileName: String): DownloadFileRecord?

    @Query("SELECT * FROM DownloadFileRecord WHERE id is :id")
    fun findById(id: Long): DownloadFileRecord?

    @Update
    fun update(downloadFileRecord: DownloadFileRecord)

    @Insert
    fun insert(downloadFileRecord: DownloadFileRecord): Long

    @Insert
    fun insertAll(vararg downloadFileRecords: DownloadFileRecord)

    @Delete
    fun delete(downloadFileRecord: DownloadFileRecord)

}