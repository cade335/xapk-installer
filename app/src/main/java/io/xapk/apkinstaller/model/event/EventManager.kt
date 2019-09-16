package io.xapk.apkinstaller.model.event

import org.greenrobot.eventbus.EventBus

object EventManager {

    fun register(subscriber: Any) {
        EventBus.getDefault().register(subscriber)
    }

    fun unregister(subscriber: Any) {
        EventBus.getDefault().unregister(subscriber)
    }

    fun post(event: Any) {
        EventBus.getDefault().post(event)
    }
}
