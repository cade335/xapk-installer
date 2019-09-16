package io.xapk.apkinstaller.model.glide

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentCallbacks2
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.CheckResult
import androidx.annotation.WorkerThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.TransitionOptions
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import io.xapk.apkinstaller.app.App
import io.xapk.apkinstaller.model.glide.lister.ImageLoadStateListener
import java.io.File
import java.util.concurrent.ExecutionException

object ImageLoader {
    private fun <T> getRequestManager(mContext: T): RequestManager {
        var requestManager: RequestManager? = null
        try {
            if (mContext is Fragment && mContext.isAdded) {
                requestManager = Glide.with(mContext)
            } else if (mContext is android.app.Fragment && mContext.isAdded) {
                requestManager = Glide.with(mContext)
            } else if (mContext is View) {
                requestManager = Glide.with(mContext)
            } else if (mContext is FragmentActivity) {
                requestManager = Glide.with(mContext)
            } else if (mContext is Activity) {
                requestManager = Glide.with(mContext)
            } else if (mContext is Context) {
                requestManager = Glide.with(mContext)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            requestManager = requestManager ?: Glide.with(App.mContext)
        }
        return requestManager
    }

    internal class Builder<T> constructor(mContext: T, private var url: Any) {
        private val requestManager = getRequestManager(mContext)
        private val requestBuilder: RequestBuilder<Drawable> = requestManager.load(url)

        constructor(mContext: T) : this(mContext, String())

        @CheckResult
        @SuppressLint("CheckResult")
        fun getRequestManager() = requestManager

        @CheckResult
        @SuppressLint("CheckResult")
        fun setDefaultRequestOptions(): Builder<T> {
            requestBuilder.apply(defaultRequestOptions())
            return this
        }

        @CheckResult
        @SuppressLint("CheckResult")
        fun setRequestOptions(requestOptions: RequestOptions): Builder<T> {
            requestBuilder.apply(requestOptions)
            return this
        }

        @CheckResult
        @SuppressLint("CheckResult")
        fun optionalCenterCrop(): Builder<T> {
            requestBuilder.optionalCenterCrop()
            return this
        }

        @CheckResult
        @SuppressLint("CheckResult")
        fun optionalCircleCrop(): Builder<T> {
            requestBuilder.optionalCircleCrop()
            return this
        }

        @CheckResult
        @SuppressLint("CheckResult")
        fun setTransition(transitionOptions: TransitionOptions<*, in Drawable>): Builder<T> {
            requestBuilder.transition(transitionOptions)
            return this
        }

        @CheckResult
        @SuppressLint("CheckResult")
        fun transform(vararg transformations: Transformation<Bitmap>): Builder<T> {
            requestBuilder.transform(*transformations)
            return this
        }

        @CheckResult
        @SuppressLint("CheckResult")
        fun skipMemoryCache(): Builder<T> {
            requestBuilder.skipMemoryCache(true)
            return this
        }

        @CheckResult
        @SuppressLint("CheckResult")
        fun setThumbnail(thumbnailRequest: RequestBuilder<Drawable>): Builder<T> {
            requestBuilder.thumbnail(thumbnailRequest)
            return this
        }

        @CheckResult
        @SuppressLint("CheckResult")
        fun setImageLoadStateLister(imageLoadStateListener: ImageLoadStateListener): Builder<T> {
            requestBuilder.listener(imageLoadStateListener)
            return this
        }

        fun getRequestBuilder(): RequestBuilder<Drawable> {
            return requestBuilder
        }

        fun build(imageView: ImageView) {
            requestBuilder.into(imageView)
        }
    }

    fun defaultRequestOptions(): RequestOptions {
        return RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
    }

    fun defaultRequestOptions(placeholderId: Int, errorId: Int? = null): RequestOptions {
        val errorId1 = errorId ?: placeholderId
        return defaultRequestOptions().placeholder(placeholderId).error(errorId1)
    }

    fun defaultRequestOptions(placeholderDrawable: Drawable, errorDrawable: Drawable? = null): RequestOptions {
        val errorDrawable1 = errorDrawable ?: placeholderDrawable
        return defaultRequestOptions().placeholder(placeholderDrawable).error(errorDrawable1)
    }

    fun defaultTransitionOptions(): DrawableTransitionOptions {
        return DrawableTransitionOptions().crossFade(150)
    }

    @WorkerThread
    @Throws(ExecutionException::class, InterruptedException::class)
    fun <T> downloadImage(mContext: T, url: String): File {
        return getRequestManager(mContext)
                .load(url)
                .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .get()
    }

    @WorkerThread
    @Throws(ExecutionException::class, InterruptedException::class)
    fun <T> getCacheFile(mContext: T, url: Any): File {
        return getRequestManager(mContext)
                .downloadOnly()
                .load(url)
                .submit()
                .get()
    }

    @WorkerThread
    @Throws(ExecutionException::class, InterruptedException::class)
    fun <T> getCacheBitmap(mContext: T, url: Any): Bitmap {
        return getRequestManager(mContext)
                .asBitmap()
                .load(url)
                .submit()
                .get()
    }

    @WorkerThread
    @Throws(ExecutionException::class, InterruptedException::class)
    fun <T> getGifDrawable(mContext: T, url: Any): GifDrawable {
        return getRequestManager(mContext)
                .asGif()
                .load(url)
                .submit()
                .get()
    }

    fun getPhotoCacheDir(mContext: Context): File? {
        return Glide.getPhotoCacheDir(mContext)
    }

    fun clearMemory(mContext: Context) {
        Glide.get(mContext).clearMemory()
        Glide.get(mContext).trimMemory(ComponentCallbacks2.TRIM_MEMORY_COMPLETE)
    }

    fun <T> pauseRequests(mContext: T) {
        getRequestManager(mContext).pauseRequests()
    }

    fun <T> resumeRequests(mContext: T) {
        getRequestManager(mContext).resumeRequests()
    }

    @WorkerThread
    fun clearDiskCache(mContext: Context) {
        Glide.get(mContext).clearDiskCache()
    }
}
