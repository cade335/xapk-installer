package io.xapk.apkinstaller.model.glide.lister

import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import androidx.annotation.MainThread

import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

abstract class ImageLoadStateListener : RequestListener<Drawable> {
    private val handler by lazy { Handler(Looper.getMainLooper()) }
    @MainThread
    protected abstract fun onResourceReady(resource: Drawable)

    @MainThread
    protected abstract fun onLoadFailed()

    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
        handler.post { onLoadFailed() }
        return false
    }

    override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
        handler.post { onResourceReady(resource) }
        return false
    }
}
