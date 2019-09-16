package io.xapk.apkinstaller.utils.io

import androidx.annotation.WorkerThread
import java.io.*

object FileWriterUtils {
    interface FileWriterProgressCallback {
        fun onProgress(currentOffset: Long)
    }

    fun writeStringToFile(documentFile: File, contentStr: String) {
        writeStringToFile(documentFile, false, arrayOf(contentStr))
    }

    fun writeStringToFile(documentFile: File, lineFeed: Boolean, contentArrays: Array<String>) {
        var pw: PrintWriter? = null
        try {
            PrintWriter(documentFile).apply {
                pw = this
                contentArrays.forEach {
                    if (lineFeed) {
                        this.println(it)
                    } else {
                        this.print(it)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                pw?.flush()
                pw?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @WorkerThread
    fun writeFileFromIS(newFile: File, inputStreams: InputStream, fileWriterProgressCallback: FileWriterProgressCallback? = null): Boolean {
        var isSuccess = false
        var os: BufferedOutputStream? = null
        try {
            os = BufferedOutputStream(FileOutputStream(newFile))
            val data = ByteArray(1024 * 16)
            var len: Int
            var currentOffset = 0L
            while (inputStreams.read(data).apply { len = this } != -1) {
                os.write(data, 0, len)
                currentOffset += len
                fileWriterProgressCallback?.onProgress(currentOffset)
            }
            isSuccess = true
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                os?.close()
                inputStreams.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return isSuccess
    }
}