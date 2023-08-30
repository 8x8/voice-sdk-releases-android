package com.wavecell.sample.app.log;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
        AppComponentTag.TOKEN_SET_AND_UPDATE,
        AppComponentTag.INCOMING_CALL,
        AppComponentTag.PLACING_CALL,
        AppComponentTag.ACCOUNT_INFO,
        AppComponentTag.LOG_DATA,
        AppComponentTag.NOTIFICATION,
        AppComponentTag.UI,
})
public @interface AppComponentTag {
    String TOKEN_SET_AND_UPDATE = "TokenSetAndUpdate";

    String ACCOUNT_INFO = "AccountInfo";

    String LOG_DATA = "LogData";
    String UI = "UI";

    String INCOMING_CALL = "Incoming call";

    String PLACING_CALL = "Placing call";

    String NOTIFICATION = "notification";
}

