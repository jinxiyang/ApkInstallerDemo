package com.yang.apkinstaller

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import okhttp3.internal.closeQuietly

/**
 * 文件工具类（安卓10及以上，API>=29)
 *
 *
 * 只能操作自己APP的文件
 *
 * 暂时卸载了，又重新安装，也不能操作 TODO 待解决
 */
object FileUtils {

    // Checks if a volume containing external storage is available
    // for read and write.
    fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    // Checks if a volume containing external storage is available to at least read.
    fun isExternalStorageReadable(): Boolean {
        return Environment.getExternalStorageState() in
                setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
    }


    /**
     * 查询文件，返回Uri
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    fun queryFile(context: Context, contentUri: Uri, fileName: String, relativePath: String? = null): Uri? {
        val projection = arrayOf(MediaStore.MediaColumns._ID)

        val selection: String
        val selectionArgs: Array<String>

        if (relativePath.isNullOrEmpty()) {
            selection = "${MediaStore.MediaColumns.DISPLAY_NAME} =?"
            selectionArgs = arrayOf(fileName)
        } else {
            selection = "${MediaStore.MediaColumns.DISPLAY_NAME} =? and ${MediaStore.MediaColumns.RELATIVE_PATH} =?"
            selectionArgs = arrayOf(fileName, relativePath)
        }

        val cursor = context.contentResolver.query(contentUri, projection, selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns._ID)
            val id = cursor.getLong(columnIndex)
            cursor.closeQuietly()
            return ContentUris.withAppendedId(contentUri, id)
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun createNewFile(context: Context, contentUri: Uri, fileName: String): Uri?{
        val index = fileName.lastIndexOf('.')
        var mimeType: String? = null
        if (index > 0 && index < fileName.length - 1) {
            //有字符点（.），不在第一个也不在最后一个
//            mimeType = MyMimeTypeUtils.getMimeType(fileName.substring(index + 1))
        }
//        return createNewFile(context, contentUri, fileName, mimeType)
        return createNewFile(context, contentUri, fileName, "application/vnd.android.package-archive")
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun createNewFile(context: Context, contentUri: Uri, fileName: String, mimeType: String? = null,
                      relativePath: String? = null): Uri?{
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        if (!mimeType.isNullOrEmpty()) {
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        }
        //子文件夹, 如：Download/AppInstallerDemo，会在子文件AppInstallerDemo中
        if (!relativePath.isNullOrEmpty()) {
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
        }
        return context.contentResolver.insert(contentUri, contentValues)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun deleteFile(context: Context, fileUri: Uri): Int {
        return context.contentResolver.delete(fileUri, null, null)
    }

}