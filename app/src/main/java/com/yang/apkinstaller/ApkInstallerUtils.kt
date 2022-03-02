package com.yang.apkinstaller

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageInstaller
import android.net.Uri
import android.util.Log
import okhttp3.internal.closeQuietly

object ApkInstallerUtils {

    fun installApk(activity: Activity, uri: Uri) {
        //manifest 需要添加权限：android.permission.REQUEST_INSTALL_PACKAGES
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        activity.startActivity(intent)
    }

    fun install(activity: Activity, uri: Uri){
        val packageInstaller = activity.packageManager.packageInstaller
        val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
        val sessionId = packageInstaller.createSession(params)
        val session = packageInstaller.openSession(sessionId)

        val inputStream = activity.contentResolver.openInputStream(uri)
        if (inputStream == null) {
            Log.i("======", "install openInputStream: failed")
            return
        }
        val name = "${System.currentTimeMillis()}.apk"
        val outputStream = session.openWrite(name, 0, -1)

        val buffer = ByteArray(4096)
        var length = 0
        try {
            while (length != -1) {
                length = inputStream.read(buffer)
                if (length > 0) {
                    outputStream.write(buffer, 0, length)
                }
            }
        } finally {
            inputStream.closeQuietly()
            outputStream.closeQuietly()
        }

        //回调DownloadListActivity.onNewIntent，处理继续跳转安装，或者处理安装失败原因
        val intent = Intent(activity, DownloadListActivity::class.java)
        val pending = PendingIntent.getActivity(activity, 0, intent, 0)
        val intentSender = pending.intentSender
        session.commit(intentSender)
    }
}