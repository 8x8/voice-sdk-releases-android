package com.wavecell.sample.app.log

import java.io.File
import java.io.FilenameFilter

class StartsWithFilenameFilter internal constructor(private val prefix: String) : FilenameFilter {

    override fun accept(dir: File, filename: String): Boolean {
        return filename.startsWith(prefix)
    }
}