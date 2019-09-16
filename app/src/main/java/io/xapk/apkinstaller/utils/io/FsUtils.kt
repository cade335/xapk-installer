package io.xapk.apkinstaller.utils.io

import android.os.Environment
import android.text.TextUtils
import androidx.annotation.WorkerThread
import java.io.File
import java.io.FileFilter
import java.io.FileInputStream
import java.util.*

object FsUtils {

    val isSdUsable: Boolean
        get() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    fun getStorageDir(): File? {
        return if (isSdUsable) {
            Environment.getExternalStorageDirectory()
        } else {
            null
        }
    }

    fun exists(filePath: String?): Boolean {
        return !TextUtils.isEmpty(filePath) && exists(File(filePath))
    }

    fun exists(file: File?): Boolean {
        return file != null && file.exists()
    }

    fun isFile(filePath: String?): Boolean {
        val file = File(filePath)
        return exists(file) && file.isFile
    }

    fun deleteFileOrDir(filePath: String?) {
        filePath?.let {
            deleteFileOrDir(File(it))
        }
    }

    fun deleteFileOrDir(file: File?) {
        if (file != null && exists(file)) {
            if (file.isFile) {
                file.delete()
            } else if (file.isDirectory) {
                file.listFiles()?.forEach {
                    deleteFileOrDir(it)
                }
                file.delete()
            }
        }
    }

    fun createOnNotFound(folder: File?): File? {
        if (folder == null) {
            return null
        }
        if (!exists(folder)) {
            folder.mkdirs()
        }
        return if (exists(folder)) {
            folder
        } else {
            null
        }
    }

    @WorkerThread
    fun getDirFilesArray(file: File?, fileFilter: FileFilter? = null): ArrayList<File> {
        val listFile = ArrayList<File>()
        file?.apply {
            if (this.isFile) {
                if (fileFilter != null) {
                    if (fileFilter.accept(this)) {
                        listFile.add(this)
                    }
                } else {
                    listFile.add(this)
                }
            } else if (this.isDirectory) {
                this.listFiles()?.forEach {
                    listFile.addAll(getDirFilesArray(it, fileFilter))
                }
            }
        }
        return listFile
    }

    fun getFileOrDirLength(filePath: String?): Long {
        return if (!TextUtils.isEmpty(filePath)) {
            this.getFileOrDirLength(File(filePath))
        } else {
            0L
        }
    }

    fun getFileOrDirLength(dirFile: File?): Long {
        var length = 0L
        if (dirFile != null && exists(dirFile)) {
            if (dirFile.isFile) {
                length += dirFile.length()
            } else if (dirFile.isDirectory) {
                getDirFilesArray(dirFile).forEach {
                    length += getFileOrDirLength(it)
                }
            }
        }
        return length
    }

    @WorkerThread
    fun copyFileOrDirToDir(newFileDir: File, oldFile: File) {
        if (!this.exists(oldFile)) {
            return
        }
        this.createOnNotFound(newFileDir)
        if (!this.exists(newFileDir)) {
            return
        }
        if (oldFile.isDirectory) {
            val tempDir = File(newFileDir, oldFile.name)
            this.createOnNotFound(tempDir)
            oldFile.listFiles()?.forEach {
                copyFileOrDirToDir(tempDir, it)
            }
        } else if (oldFile.isFile) {
            try {
                FileWriterUtils.writeFileFromIS(File(newFileDir, oldFile.name), FileInputStream(oldFile))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @WorkerThread
    fun writeFileToFile(newFile: File, oldFile: File) {
        if (!this.exists(oldFile)) {
            return
        }
        this.createOnNotFound(newFile.parentFile)
        if (!this.exists(newFile.parentFile)) {
            return
        }
        if (oldFile.isFile) {
            try {
                FileWriterUtils.writeFileFromIS(newFile, FileInputStream(oldFile))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
