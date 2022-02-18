package com.yang.apkinstaller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private lateinit var btnDownloadApk: Button
    private lateinit var btnDownloadList: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnDownloadApk = findViewById(R.id.btnDownloadApk)
        btnDownloadList = findViewById(R.id.btnDownloadList)

        btnDownloadApk.setOnClickListener {
            downloadApk()
        }

        btnDownloadList.setOnClickListener {
            navigateDownloadList()
        }
    }

    private fun downloadApk(){


    }

    private fun navigateDownloadList(){

    }
}