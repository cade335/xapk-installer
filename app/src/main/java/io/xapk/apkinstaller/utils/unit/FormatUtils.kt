package io.xapk.apkinstaller.utils.unit

import java.text.DecimalFormat

object FormatUtils {
    fun formatFileLength(sizeBytes: Long): String {
        if (sizeBytes <= 0) {
            return "0B"
        }
        val units = arrayOf("B", "KB", "MB", "GB", "TB", "EB")
        val digitGroups = (Math.log10(sizeBytes.toDouble()) / Math.log10(1024.0)).toInt()
        return DecimalFormat("#.##").format(sizeBytes / Math.pow(1024.0, digitGroups.toDouble())) + " " + units[digitGroups]
    }

    fun formatPercent(progress: Long, count: Long): Int {
        return if (count < progress) {
            0
        } else {
            (progress * 1f / count * 100f).toInt()
        }
    }
}
