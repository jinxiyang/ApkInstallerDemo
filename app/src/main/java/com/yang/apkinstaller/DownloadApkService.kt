package com.yang.apkinstaller

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import com.yang.apkinstaller.database.bean.DownloadFileRecord
import com.yang.apkinstaller.storage.SharedStorage
import okhttp3.*
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * 访问外部存储空间，权限：
 * Android 10（API 29）不需要请求任何权限，使用分区存储框架
 * Android 9 （API 28）需要权限，READ_EXTERNAL_STORAGE、WRITE_EXTERNAL_STORAGE
 */
class DownloadApkService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return DownloadStateBinder()
    }

    public class DownloadStateBinder : Binder() {
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val downloadUrl = it.getStringExtra(DOWNLOAD_URL)
            val autoInstallApk = it.getBooleanExtra(AUTO_INSTALL_APK, true)
            if (!downloadUrl.isNullOrEmpty()) {
                recordDatabase(downloadUrl)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun recordDatabase(downloadUrl: String) {
        Thread {
            val fileName: String = getFileName(downloadUrl)
            val downloadFileRecord = DownloadFileRecord(
                0,
                downloadUrl,
                null,
                null,
                fileName,
                DownloadFileRecord.STATE_DOWNLOAD_READY
            )
            val dao = (application as ApkInstallerApplication).db.downloadFileRecordDao()
            val id = dao.insert(downloadFileRecord)
            downloadApk(id, fileName, downloadUrl)
        }.start()
    }

    private fun downloadApk(id: Long, fileName: String, downloadUrl: String) {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
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
                updateRecord(id, DownloadFileRecord.STATE_DOWNLOAD_FAILED)
            }

            override fun onResponse(call: Call, response: Response) {
                val byteStream = response.body?.byteStream()
                if (byteStream != null) {
                    Log.i("======", "onResponse: 正在下载……")

                    try {
                        val uri = createFileUri(fileName)
                        if (uri == null) {
                            Log.i("======", "onResponse: uri null ")
                            updateRecord(id, DownloadFileRecord.STATE_DOWNLOAD_FAILED)
                            return
                        }
                        updateRecord(id, uri, DownloadFileRecord.STATE_DOWNLOADING)
                        val openOutputStream = contentResolver.openOutputStream(uri)
                        val bos = BufferedOutputStream(openOutputStream)
                        val bis = BufferedInputStream(byteStream)
                        writeData(bis, bos)
                        Log.i("======", "onResponse: 下载完成了")
                        updateRecord(id, DownloadFileRecord.STATE_DOWNLOADED)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.i("======", "onResponse: 下载失败 " + e.message)
                        updateRecord(id, DownloadFileRecord.STATE_DOWNLOAD_FAILED)
                    }
                } else {
                    Log.i("======", "onResponse: 下载失败，获取不到数据")
                    updateRecord(id, DownloadFileRecord.STATE_DOWNLOAD_FAILED)
                }
            }
        })
    }

    private fun updateRecord(id: Long, state: Int) {
        val dao = (application as ApkInstallerApplication).db.downloadFileRecordDao()
        dao.findById(id)?.let {
            it.state = state
            dao.update(it)
        }
    }

    private fun updateRecord(id: Long, uri: Uri?, state: Int) {
        val dao = (application as ApkInstallerApplication).db.downloadFileRecordDao()
        dao.findById(id)?.let {
            it.state = state
            it.uri = uri?.toString()
            dao.update(it)
        }
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

    private fun createFileUri(fileName: String): Uri?{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val fileUri = SharedStorage.queryFile(this, MediaStore.Downloads.EXTERNAL_CONTENT_URI, fileName)
            if (fileUri != null) {
                SharedStorage.deleteFile(this, fileUri)
            }
            SharedStorage.createNewFile(this, MediaStore.Downloads.EXTERNAL_CONTENT_URI, fileName)
        } else {
            val filesDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val file = File(filesDir, fileName)
            Uri.fromFile(file)
        }
    }

    private fun writeData(bis: BufferedInputStream, bos: BufferedOutputStream) {
        val buffer = ByteArray(4096)
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