package io.xapk.apkinstaller.utils.io

import androidx.annotation.WorkerThread
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
import java.io.*

object ZipUtils {
    interface IProgress {
        fun onStart()
        fun onProgress(currentOffset: Long)
        fun onComped()
        fun onError()
    }

    @WorkerThread
    fun composeFileOrDir(outputFile: File, oldFile: File, fistFileName: String = String()) {
        var zipArchiveOutputStream: ZipArchiveOutputStream? = null
        try {
            FsUtils.createOnNotFound(outputFile.parentFile)
            if (!FsUtils.exists(outputFile.parentFile)) {
                return
            }
            zipArchiveOutputStream = ZipArchiveOutputStream(outputFile)
            zipArchiveOutputStream.setComment("io.xapk.apkinstaller")
            writeZip(zipArchiveOutputStream, oldFile, fistFileName)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                zipArchiveOutputStream?.apply {
                    this.closeArchiveEntry()
                    this.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @WorkerThread
    @Throws(IOException::class)
    private fun writeZip(
        zipArchiveOutputStream: ZipArchiveOutputStream,
        oldFile: File,
        parentName: String
    ) {
        if (!FsUtils.exists(oldFile) || !oldFile.canRead()) {
            return
        }
        if (oldFile.isDirectory) {
            val parentName2 = if (parentName.isNotEmpty()) {
                "$parentName${File.separator}"
            } else {
                parentName
            }
            val oldFileArrays = oldFile.listFiles()
            if (oldFileArrays.isNullOrEmpty()) {
                zipArchiveOutputStream.putArchiveEntry(ZipArchiveEntry(parentName2))
            } else {
                oldFileArrays.forEach {
                    writeZip(zipArchiveOutputStream, it, "$parentName2${it.name}")
                }
            }
        } else if (oldFile.isFile) {
            zipArchiveOutputStream.putArchiveEntry(ZipArchiveEntry(parentName))
            val bufferedInputStream = BufferedInputStream(FileInputStream(oldFile))
            val data = ByteArray(1024 * 16)
            var len: Int
            var currentOffset = 0L
            while (bufferedInputStream.read(data).apply { len = this } != -1) {
                zipArchiveOutputStream.write(data, 0, len)
                currentOffset += len
            }
            bufferedInputStream.close()
        }
    }

    fun unZip(outPathFile: File, filePathFile: File, iProgress: IProgress? = null) {
        var zipInputStream: ZipArchiveInputStream? = null
        try {
            FsUtils.deleteFileOrDir(outPathFile)
            FsUtils.createOnNotFound(outPathFile)
            val outPath = outPathFile.absolutePath
            val filePath = filePathFile.absolutePath
            iProgress?.onStart()
            if (!FsUtils.exists(filePathFile) || !FsUtils.exists(outPathFile)) {
                iProgress?.onError()
                return
            }
            zipInputStream = ZipArchiveInputStream(FileInputStream(filePath))
            var zipEntry: ArchiveEntry?=null
            var szName: String
            var count = 0L
            while (zipInputStream.nextZipEntry?.apply { zipEntry = this } != null) {
                szName = zipEntry!!.name
                var currentParentFile: String
                if (zipEntry!!.isDirectory) {
                    szName = szName.substring(0, szName.length - 1)
                    currentParentFile = outPath + File.separator + szName
                    File(currentParentFile).mkdirs()
                } else {
                    val file = File("$outPath${File.separator}$szName")
                    if (!file.exists()) {
                        file.parentFile.mkdirs()
                        file.createNewFile()
                    }
                    val out = BufferedOutputStream(FileOutputStream(file))
                    var len: Int
                    val buffer = ByteArray(1024 * 16)
                    while (zipInputStream.read(buffer).apply { len = this } != -1) {
                        count += len.toLong()
                        iProgress?.onProgress(count)
                        out.write(buffer, 0, len)
                        out.flush()
                    }
                    out.close()
                }
            }
            iProgress?.onComped()
        } catch (exc: Exception) {
            exc.printStackTrace()
            iProgress?.onError()
        } finally {
            try {
                zipInputStream?.close()
            } catch (e: Exception) {
                e.printStackTrace()
                iProgress?.onError()
            }
        }
    }
}