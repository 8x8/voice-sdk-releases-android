package com.wavecell.sample.app.log

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object IOUtils {

    @Throws(IOException::class)
    fun copy(`in`: InputStream, out: OutputStream) {
        val buf = ByteArray(2048)
        var len: Int
        while (`in`.read(buf).also { len = it } != -1) {
            out.write(buf, 0, len)
        }
    }
}