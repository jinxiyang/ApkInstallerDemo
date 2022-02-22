package com.yang.apkinstaller

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast

class MainActivity : BaseActivity() {

    private lateinit var btnDownloadApk1: Button
    private lateinit var btnDownloadApk2: Button
    private lateinit var btnDownloadList: Button


    private var apkUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnDownloadApk1 = findViewById(R.id.btnDownloadApk1)
        btnDownloadApk2 = findViewById(R.id.btnDownloadApk2)
        btnDownloadList = findViewById(R.id.btnDownloadList)

        btnDownloadApk1.setOnClickListener {
            downloadApk("")
        }


        btnDownloadApk2.setOnClickListener {
            downloadApk("")
        }

        btnDownloadList.setOnClickListener {
            navigateDownloadList()
        }
    }

    private fun downloadApk(url: String) {
        //下载需要存储权限
//        val permissions = arrayOf(
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
//        )
//        if (hasDangerousPermissions(permissions)) {
        downloadApkActually(url)
//        } else {
//            apkUrl = url
//            requestDangerousPermissions(permissions, REQUEST_CODE_STORAGE_PERMISSION)
//        }
    }

    override fun handlePermissionResult(isGranted: Boolean, requestCode: Int): Boolean {
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (isGranted) {
                apkUrl?.let {
                    downloadApkActually(it)
                }
                apkUrl = null
            } else {
                Toast.makeText(this, "没有存储权限，不能下载安装包", Toast.LENGTH_SHORT).show()
            }
            return true
        }
        return super.handlePermissionResult(isGranted, requestCode)
    }


    private fun downloadApkActually(url: String) {
        Log.i("======", "downloadApkActually: $url")

        val intent = Intent(this, DownloadApkService::class.java)
        intent.putExtra("downloadUrl", url)
        startService(intent)
    }

    private fun navigateDownloadList() {

    }

    companion object {
        val REQUEST_CODE_STORAGE_PERMISSION = 0x123
    }
}