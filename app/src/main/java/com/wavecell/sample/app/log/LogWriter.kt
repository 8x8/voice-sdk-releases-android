package com.wavecell.sample.app.log

import com.wavecell.sample.app.models.SampleAppLog

interface LogWriter {

    fun log(log: SampleAppLog)
    fun cleanup()
}