package io.xapk.apkinstaller.misc

import androidx.recyclerview.widget.RecyclerView
import io.xapk.apkinstaller.model.glide.ImageLoader

import java.security.AccessController.getContext

class LoadImageDefaultScrollListener : RecyclerView.OnScrollListener() {
    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
            ImageLoader.pauseRequests(getContext())
        } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            ImageLoader.resumeRequests(getContext())
        }
    }
}