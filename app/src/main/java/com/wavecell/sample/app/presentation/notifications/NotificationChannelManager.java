package com.wavecell.sample.app.presentation.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioAttributes;
import android.net.Uri;
import android.text.TextUtils;

import com.wavecell.sample.app.R;

import java.io.File;
import java.util.Objects;

public class NotificationChannelManager {

    public void initChannels(Context context, NotificationManager notificationManager) {
        createAllNotificationChannels(context, notificationManager);
    }

    private void createAllNotificationChannels(Context context, NotificationManager notificationManager) {
        createIncomingCallChannel(context, notificationManager);
        createOngoingCallChannel(context);
        createBackgroundProcessingNotificationChannel(context);
    }

    private void createBackgroundProcessingNotificationChannel(Context context) {
        CharSequence name = context.getString(R.string.channel_name_background_processing);
        String description = context.getString(R.string.channel_description_background_processing);
        int importance = NotificationManager.IMPORTANCE_MIN;
        NotificationChannel channel = new NotificationChannel(NotificationChannelId.BACKGROUND_PROCESSING, name, importance);
        channel.setDescription(description);
        channel.setSound(null, null);
        channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
        channel.setBypassDnd(false);
        channel.setShowBadge(true);
        channel.enableVibration(false);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        if (notificationManager != null) notificationManager.createNotificationChannel(channel);
    }

    private NotificationChannel createNotificationsChannel(Context context) {
        AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE).build();
        long[] vibration = {0L};
        NotificationChannel channel =
                new NotificationChannel(
                        NotificationChannelId.INCOMING_CALLS,
                        context.getString(R.string.channel_name_incoming_call),
                        NotificationManager.IMPORTANCE_HIGH);
        channel.enableLights(true);
        channel.setVibrationPattern(vibration);
        Uri appSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + File.pathSeparator + File.separator + File.separator + context.getPackageName() + File.separator + R.raw.upbeat_loop_ringtone);
        channel.setSound(appSound, audioAttributes);
        return channel;
    }

    private void createIncomingCallChannel(Context context, NotificationManager notificationManager) {
        NotificationChannel channel = createNotificationsChannel(context);
        notificationManager.createNotificationChannel(channel);
    }


    private void createOngoingCallChannel(Context context) {
        NotificationChannel channel =
                new NotificationChannel(
                        NotificationChannelId.ONGOING_CALLS,
                        context.getString(R.string.channel_name_ongoing_call),
                        NotificationManager.IMPORTANCE_DEFAULT);
        channel.setShowBadge(false);
        channel.enableLights(false);
        channel.enableVibration(false);
        channel.setVibrationPattern(new long[]{0});
        channel.setSound(null, null);
        Objects.requireNonNull(context.getSystemService(NotificationManager.class)).createNotificationChannel(channel);
    }

    public boolean isNotificationEnabled(Context context, String channelId) {
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if(manager != null) {
            if (!TextUtils.isEmpty(channelId)) {
                NotificationChannel channel = manager.getNotificationChannel(channelId);
                return channel != null && channel.getImportance() != NotificationManager.IMPORTANCE_NONE;
            }
        }
        return false;
    }
}