package io.xapk.apkinstaller.model.bean

class ApiException constructor(throwable: Throwable, var code: Int, var displayMessage: String?)
    : Throwable(throwable) {
    constructor(throwable: Throwable) : this(throwable, 0, String())
    constructor(throwable: Throwable, code: Int) : this(throwable, code, String())
    constructor(displayMessage: String) : this(Throwable(displayMessage), 0, displayMessage)
}