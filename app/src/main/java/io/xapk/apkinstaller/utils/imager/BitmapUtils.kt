package io.xapk.apkinstaller.utils.imager

import android.graphics.Bitmap
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

object BitmapUtils {
    fun bitmap2InputStream(bm: Bitmap): InputStream {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos)
        return ByteArrayInputStream(baos.toByteArray())
    }
}
