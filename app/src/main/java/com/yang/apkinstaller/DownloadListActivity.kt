package com.yang.apkinstaller

import android.os.Bundle
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
}