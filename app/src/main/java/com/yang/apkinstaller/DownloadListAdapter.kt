package com.yang.apkinstaller

import android.app.Activity
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yang.apkinstaller.database.bean.DownloadFileRecord

class DownloadListAdapter
    : ListAdapter<DownloadFileRecord, DownloadListAdapter.DownloadFileRecordViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DownloadFileRecordViewHolder {
        return DownloadFileRecordViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: DownloadFileRecordViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }


    class DownloadFileRecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val btnAction: Button = itemView.findViewById(R.id.btnAction)
        private val tvFileName: TextView = itemView.findViewById(R.id.tvFileName)
        private val tvDownloadUrl: TextView = itemView.findViewById(R.id.tvDownloadUrl)

        fun bind(downloadFileRecord: DownloadFileRecord) {
            tvFileName.text = downloadFileRecord.fileName
            tvDownloadUrl.text = downloadFileRecord.downloadUrl
            btnAction.text = downloadFileRecord.state.toString()

            btnAction.setOnClickListener {
                if (downloadFileRecord.state == DownloadFileRecord.STATE_DOWNLOADED) {
                    val uriStr = downloadFileRecord.uri
                    val uri = Uri.parse(uriStr)
                    ApkInstallerUtils.installApk(itemView.context as Activity, uri)
                }
            }
        }

        companion object {

            fun create(parent: ViewGroup) : DownloadFileRecordViewHolder {

                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_download_list, parent, false)

                return DownloadFileRecordViewHolder(view)
            }
        }
    }

    companion object {

        private val COMPARATOR = object : DiffUtil.ItemCallback<DownloadFileRecord>(){
            override fun areItemsTheSame(
                oldItem: DownloadFileRecord,
                newItem: DownloadFileRecord
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: DownloadFileRecord,
                newItem: DownloadFileRecord
            ): Boolean {
                return oldItem.fileName == newItem.fileName
                        && oldItem.state == newItem.state
            }
        }
    }
}