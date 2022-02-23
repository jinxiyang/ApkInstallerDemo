package com.yang.apkinstaller

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import com.yang.apkinstaller.storage.SharedStorage
import okhttp3.*
import java.io.*
import java.util.concurrent.TimeUnit

/**
 * 访问外部存储空间，权限：
 * Android 10（API 29）不需要请求任何权限，使用分区存储框架
 * Android 9 （API 28）需要权限，READ_EXTERNAL_STORAGE、WRITE_EXTERNAL_STORAGE
 */
class DownloadApkService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val downloadUrl = it.getStringExtra(DOWNLOAD_URL)
            val autoInstallApk = it.getBooleanExtra(AUTO_INSTALL_APK, true)
            if (!downloadUrl.isNullOrEmpty()) {
                downloadApk(downloadUrl)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun downloadApk(downloadUrl: String) {
        val fileName = getFileName(downloadUrl)
        Log.i("======", "downloadApk1: $fileName")

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.MILLISECONDS)
            .build()

        val request = Request.Builder()
            .url(downloadUrl)
            .get()
            .build()

        val callback = okHttpClient.newCall(request)
        callback.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                Log.i("======", "onFailure: 下载失败 "  + e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                val byteStream = response.body?.byteStream()
                if (byteStream != null) {
                    Log.i("======", "onResponse: 正在下载……")
                    try {
                        val bos = createBufferedOutputStream(fileName) ?: return
                        val bis = BufferedInputStream(byteStream)
                        writeData(bis, bos)
                        Log.i("======", "onResponse: 下载完成了")
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.i("======", "onResponse: 下载失败 " + e.message)
                    }
                } else {
                    Log.i("======", "onResponse: 下载失败，获取不到数据")
                }
            }
        })
    }

    private fun getFileName(url: String): String{
        var fileName: String = url

        val index = fileName.lastIndexOf('/')
        if (index > -1 && index < fileName.length - 1) {
            //最后一个/，且不是最后一个字符
            fileName = fileName.substring(index + 1)
        }
        return fileName
    }

    private fun createBufferedOutputStream(fileName: String): BufferedOutputStream?{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val fileUri = SharedStorage.queryFile(this, MediaStore.Downloads.EXTERNAL_CONTENT_URI, fileName)
            if (fileUri != null) {
                SharedStorage.deleteFile(this, fileUri)
            }
            val uri = SharedStorage.createNewFile(this, MediaStore.Downloads.EXTERNAL_CONTENT_URI, fileName)
            if (uri == null) {
                Log.i("======", "createNewFile fail")
                return null
            }
            val outputStream = contentResolver.openOutputStream(uri)
            if (outputStream == null) {
                Log.i("======", "openOutputStream fail")
                return null
            }
            return BufferedOutputStream(outputStream)
        } else {
            val filesDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            return BufferedOutputStream(FileOutputStream(File(filesDir, fileName)))
        }
    }

    private fun writeData(bis: BufferedInputStream, bos: BufferedOutputStream) {
        val buffer = ByteArray(1024)
        var length = 0
        while (length != -1) {
            length = bis.read(buffer)
            if (length > 0) {
                bos.write(buffer, 0, length)
            }
        }
        bos.flush()
        bos.close()
        bis.close()
    }

    companion object {
        const val DOWNLOAD_URL = "downloadUrl"
        const val AUTO_INSTALL_APK = "autoInstallApk"

        const val APP_NAME_DIRECTORY = "ApkInstallerDemo"
    }
}