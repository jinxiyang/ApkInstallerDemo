package com.yang.apkinstaller.storage

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import okhttp3.internal.closeQuietly
import java.io.InputStream
import java.io.OutputStream

/**
 * 分区存储，应用共享空间，文件工具类（安卓10及以上，API>=29)
 *
 * 1、启用分区存储，清单文件需配置：
 *    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
                       android:maxSdkVersion="28" />
 * 2、访问外部存储空间，权限：
 *    Android 10（API 29）不需要请求【任何权限】，使用分区存储框架
 *    Android 9 （API 28）需要权限，READ_EXTERNAL_STORAGE、WRITE_EXTERNAL_STORAGE
 *
 * 3、只能操作自己APP的文件，数据库会为每个文件添加一条记录，所属包名，owner_package_name = com.yang.apkinstaller
 *    应用卸载之后，owner_package_name会被清空，应用重新安装之后，无法query到卸载之前的文件。
 */
@RequiresApi(Build.VERSION_CODES.Q)
object SharedStorage {

    /**
     * 查询文件，返回Uri
     */
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

    fun createNewFile(context: Context, contentUri: Uri, fileName: String): Uri?{
        val index = fileName.lastIndexOf('.')
        var mimeType: String? = null
        if (index > 0 && index < fileName.length - 1) {
            //有字符点（.），不在第一个也不在最后一个
            mimeType = MimeTypeUtils.getMimeType(fileName.substring(index + 1))
        }
        return createNewFile(context, contentUri, fileName, mimeType)
    }

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

    fun deleteFile(context: Context, fileUri: Uri): Int {
        return context.contentResolver.delete(fileUri, null, null)
    }

    fun openInputStream(context: Context, fileUri: Uri): InputStream? {
        return context.contentResolver.openInputStream(fileUri)
    }

    fun openOutputStream(context: Context, fileUri: Uri): OutputStream? {
        return context.contentResolver.openOutputStream(fileUri)
    }
}