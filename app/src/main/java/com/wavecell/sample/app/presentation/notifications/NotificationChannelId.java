package com.wavecell.sample.app.presentation.notifications;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Centralized source of all notification channels.
 */
@Retention(RetentionPolicy.SOURCE)
@StringDef({
        NotificationChannelId.INCOMING_CALLS,
        NotificationChannelId.ONGOING_CALLS,
        NotificationChannelId.BACKGROUND_PROCESSING
})
public @interface NotificationChannelId {
    String INCOMING_CALLS = NotificationChannelIdPrefix.INCOMING_CALLS_PREFIX;
    String ONGOING_CALLS = NotificationChannelIdPrefix.ONGOING_CALLS_PREFIX;
    String BACKGROUND_PROCESSING = NotificationChannelIdPrefix.BACKGROUND_PROCESSING_PREFIX;
}