package io.xapk.apkinstaller.model.event

import io.xapk.apkinstaller.utils.bean.apk.AppInfo

class AppInstalledStatusEvent(var status: Status, var appInfo: AppInfo) {
    enum class Status {
        INSTALL,
        REMOVE
    }
}
