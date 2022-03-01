package com.yang.apkinstaller.database.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DownloadFileRecord(
    @PrimaryKey(autoGenerate = true) var id: Long,
    val downloadUrl: String?,
    var downloadedTime: String?,
    var uri: String?,
    var fileName: String,
    var state:Int
){
    companion object {
        /**
         * 准备下载
         */
        val STATE_DOWNLOAD_READY: Int = 0

        /**
         * 下载中
         */
        val STATE_DOWNLOADING = 1

        /**
         * 下载中，暂停了
         */
        val STATE_DOWNLOADING_AND_PAUSED = 2

        /**
         * 下载完成
         */
        val STATE_DOWNLOADED = 3

        /**
         * 下载失败
         */
        val STATE_DOWNLOAD_FAILED = 4

        /**
         * 下载完成，但文件找不到了
         */
        val STATE_DOWNLOADED_AND_NOT_FOUND = 5
    }
}
