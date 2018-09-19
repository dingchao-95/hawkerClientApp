package client.hawker.com.hawkerclient.Utils;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationBuilderWithBuilderAccessor;

import client.hawker.com.hawkerclient.R;

public class NotificationHelper extends ContextWrapper{
    private static final String HAWKER_CHANNEL_ID = "client.hawker.com.hawkerclient";
    private static final String HAWKER_CHANNEL_NAME = "HawkerClient";

    private NotificationManager notificationManager;

    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel hawkerChannel = new NotificationChannel(HAWKER_CHANNEL_ID,HAWKER_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        hawkerChannel.enableLights(false);
        hawkerChannel.enableVibration(true);
        hawkerChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(hawkerChannel);
    }

    public NotificationManager getManager() {
        if(notificationManager == null)
        {
            notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getHawkerNotification(String title,
                                                      String message,
                                                      Uri soundUri)
    {
        return new Notification.Builder(getApplicationContext(),HAWKER_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(soundUri)
                .setAutoCancel(true);
    }
}
