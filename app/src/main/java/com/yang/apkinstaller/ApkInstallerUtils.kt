package com.yang.apkinstaller

import android.app.Activity
import android.content.Intent
import android.net.Uri

object ApkInstallerUtils {

    fun installApk(activity: Activity, uri: Uri) {
        //manifest 需要添加权限：android.permission.REQUEST_INSTALL_PACKAGES
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        activity.startActivity(intent)
    }
}