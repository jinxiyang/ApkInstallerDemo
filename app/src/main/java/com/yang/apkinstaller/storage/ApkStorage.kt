package com.yang.apkinstaller.storage

import android.content.Context
import java.io.InputStream

object ApkStorage {
    fun openRawResource(context: Context, resId: Int): InputStream {
        return context.resources.openRawResource(resId)
    }

    fun openAssets(context: Context, fileName: String): InputStream{
        return context.resources.assets.open(fileName)
    }
}