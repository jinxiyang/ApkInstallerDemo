package com.yang.apkinstaller

import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.ArrayList

open class BaseActivity : AppCompatActivity() {

    /**
     * 是否有权限
     */
    fun hasDangerousPermissions(permissions: Array<String>): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    return false
                }
            }
        }
        return true
    }

    /**
     * 请求权限
     *
     * @param permissions
     * @param requestCode
     */
    fun requestDangerousPermissions(permissions: Array<String>, requestCode: Int) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            handlePermissionResult(true, requestCode)
            return
        }
        val needPermissions: MutableList<String> = ArrayList()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                needPermissions.add(permission)
            }
        }
        if (needPermissions.size == 0) {
            handlePermissionResult(true, requestCode)
            return
        }
        ActivityCompat.requestPermissions(this, needPermissions.toTypedArray(), requestCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        var isGranted = true
        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                isGranted = false
            }
        }
        val finish = handlePermissionResult(isGranted, requestCode)
        if (!finish) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    /**
     * 处理请求危险权限的结果
     *
     * @param isGranted   是否允许
     * @param requestCode
     * @return
     */
    open fun handlePermissionResult(isGranted: Boolean, requestCode: Int): Boolean {
        return false
    }
}