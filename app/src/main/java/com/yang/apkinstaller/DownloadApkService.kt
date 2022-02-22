package com.yang.apkinstaller

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import okhttp3.*
import java.io.*
import java.util.concurrent.TimeUnit


/**
 *
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

        val os: OutputStream
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val fileUri = FileUtils.queryFile(this, MediaStore.Downloads.EXTERNAL_CONTENT_URI, fileName)
            if (fileUri != null) {
                FileUtils.deleteFile(this, fileUri)
            }
            val uri = FileUtils.createNewFile(this, MediaStore.Downloads.EXTERNAL_CONTENT_URI, fileName)
            if (uri == null) {
                Log.i("======", "createNewFile fail")
                return
            }
            val outputStream = contentResolver.openOutputStream(uri)
            if (outputStream == null) {
                Log.i("======", "openOutputStream fail")
                return
            }
            os = outputStream
        } else {
            val filesDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            os = FileOutputStream(File(filesDir, fileName))
        }

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
            }

            override fun onResponse(call: Call, response: Response) {

                val byteStream = response.body?.byteStream()
                if (byteStream != null) {
                    Log.i("======", "onResponse: 正在下载……")
                    try {
                        val bufferedOutputStream = BufferedOutputStream(os)
                        val bufferedInputStream = BufferedInputStream(byteStream)

                        val buffer = ByteArray(1024)
                        var length = 0
                        while (length != -1) {
                            length = bufferedInputStream.read(buffer)
                            if (length > 0) {
                                bufferedOutputStream.write(buffer, 0, length)
                            }
                        }

                        bufferedOutputStream.flush()
                        bufferedOutputStream.close()
                        bufferedInputStream.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    Log.i("======", "onResponse: 下载完成了")
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

    companion object {
        const val DOWNLOAD_URL = "downloadUrl"
        const val AUTO_INSTALL_APK = "autoInstallApk"

        const val APP_NAME_DIRECTORY = "ApkInstallerDemo"
    }
}