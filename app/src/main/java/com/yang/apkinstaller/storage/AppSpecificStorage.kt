package com.yang.apkinstaller.storage

import android.content.Context
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * 应用专属存储空间，方法是代码示例
 *
 * 1、如果用户卸载应用，系统会移除保存在应用专属存储空间中的文件。
 * 2、可以使用 File API 访问和存储文件
 * 3、不需要任何系统权限即可读取和写入，内部存储空间和外部存储空间
 * 4、这些目录的空间通常比较小
 */
object AppSpecificStorage {

    fun openFileOutput(context: Context, fileName: String): FileOutputStream {
        return context.openFileOutput(fileName, Context.MODE_PRIVATE)
    }

    fun openFileInput(context: Context, fileName: String): FileInputStream {
        return context.openFileInput(fileName)
    }

    fun fileList(context: Context): Array<String>{
        return context.fileList()
    }

    /**
     * 获取目录，若没有则创建目录
     */
    fun getDir(context: Context, dirName: String): File {
        return context.getDir(dirName, Context.MODE_PRIVATE)
    }

    /**
     * 创建文件
     */
    fun createFile(context: Context, fileName: String): File{
        return File(context.filesDir, fileName)
    }

    /**
     * 创建缓存文件
     *
     * 若suffix为空，则创建后缀名为.temp的文件
     *
     * 注意：当设备的内部存储空间不足时，Android 可能会删除这些缓存文件以回收空间。因此，请在读取前检查缓存文件是否存在。
     */
    fun createTempFile(context: Context, prefix: String, suffix: String?): File{
        return File.createTempFile(prefix, suffix, context.cacheDir)
    }

    /**
     * 创建缓存文件
     *
     * 若suffix为空，则创建后缀名为.temp的文件
     *
     * 注意：当设备的内部存储空间不足时，Android 可能会删除这些缓存文件以回收空间。因此，请在读取前检查缓存文件是否存在。
     */
    fun createExternalTempFile(context: Context, prefix: String, suffix: String?): File{
        return File.createTempFile(prefix, suffix, context.externalCacheDir)
    }

    /**
     * 创建缓存文件
     *
     * 注意：当设备的内部存储空间不足时，Android 可能会删除这些缓存文件以回收空间。因此，请在读取前检查缓存文件是否存在。
     */
    fun createCacheFile(context: Context, fileName: String): File{
        return File(context.cacheDir, fileName)
    }

    /**
     * 创建缓存文件
     *
     * 注意：当设备的内部存储空间不足时，Android 可能会删除这些缓存文件以回收空间。因此，请在读取前检查缓存文件是否存在。
     */
    fun createExternalCacheFile(context: Context, fileName: String): File{
        return File(context.externalCacheDir, fileName)
    }

    fun deleteFile(context: Context, fileName: String): Boolean{
        return context.deleteFile(fileName)
    }

    /**
     *
     * 获取外部存储目录，持久性文件目录，应用专属的内部文件目录，不作为媒体文件对用户可见。
     *
     * 当应用程序卸载时，这些文件将被删除。
     *
     * 访问自己APP文件，不需要权限。
     * 访问其他APP文件，需要：android.Manifest.permission.WRITE_EXTERNAL_STORAGE、android.Manifest.permission.READ_EXTERNAL_STORAGE
     *
     * @param type Environment.DIRECTORY_MUSIC, Environment.DIRECTORY_PODCASTS,
     *             Environment.DIRECTORY_RINGTONES, Environment.DIRECTORY_ALARMS,
     *             Environment.DIRECTORY_NOTIFICATIONS, Environment.DIRECTORY_PICTURES,
     *             or Environment.DIRECTORY_MOVIES.
     */
    fun getExternalFilesDir(context: Context, type: String): File? {
        return context.getExternalFilesDir(type)
    }
}