package com.wavecell.sample.app.models

import com.wavecell.sample.app.presentation.model.CallType
import java.util.*

class CallItem(val nameOrNumber: String,
               val userId: String,
               var avatarUrl: String,
               var endTime: Long,
               val date: Long,
               val callType: CallType,
               val uniqueId: UUID) {

    override fun toString(): String {
        return "CallItem(nameOrNumber='$nameOrNumber', avatarUrl='$avatarUrl', " +
                "endTime=$endTime, date=$date, callType=$callType, uniqueId=$uniqueId)"
    }
}