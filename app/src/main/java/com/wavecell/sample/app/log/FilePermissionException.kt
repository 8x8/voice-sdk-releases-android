package com.wavecell.sample.app.log

class FilePermissionException: Exception {
    constructor(message: String, ex: Exception?) : super(message, ex)
    constructor(message: String) : super(message)
}
