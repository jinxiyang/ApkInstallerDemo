package com.yang.apkinstaller

import android.content.Intent
import android.content.pm.PackageInstaller
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yang.apkinstaller.database.bean.DownloadFileRecord

class DownloadListActivity : BaseActivity() {

    val adapter by lazy {
        DownloadListAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_list)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        Thread {
            val dao = (application as ApkInstallerApplication).db.downloadFileRecordDao()
            val all = dao.getAll()
            update(all)
        }.start()
    }


    private fun update(list: List<DownloadFileRecord>) {
        runOnUiThread {
            adapter.submitList(list)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        intent?.extras?.apply {
            val status = this.getInt(PackageInstaller.EXTRA_STATUS)
            Log.i("======", "onNewIntent: $status")
            when (status) {
                PackageInstaller.STATUS_PENDING_USER_ACTION -> {
                    //提示用户进行安装
                    val intent1 = this.get(Intent.EXTRA_INTENT) as Intent
                    startActivity(intent1)
                }
                PackageInstaller.STATUS_SUCCESS -> {
                    //安装成功
                }
                else -> {
                    //失败信息
                    val msg = this.getString(PackageInstaller.EXTRA_STATUS_MESSAGE)
                }
            }
        }
    }

}