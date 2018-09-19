package client.hawker.com.hawkerclient.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

import client.hawker.com.hawkerclient.R;
import client.hawker.com.hawkerclient.Retrofit.IHawkerAPI;
import client.hawker.com.hawkerclient.Utils.Common;
import client.hawker.com.hawkerclient.Utils.NotificationHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        if(Common.currentUser != null)
        {
            updateTokenToFireBase(s);
        }
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //implement show notification
        if(remoteMessage.getData() != null)
        {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                sendNotificationAPI26(remoteMessage);
            }
            else
            {
                sendNotification(remoteMessage);
            }
        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        //get info from message
        Map<String,String> data = remoteMessage.getData();
        String title = data.get("title");
        String message = data.get("message");

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri);

        NotificationManager notif = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notif.notify(new Random().nextInt(),builder.build());

    }

    private void sendNotificationAPI26(RemoteMessage remoteMessage) {
        //from api 26 we need to implement the notif channel.
        //get info from message
        Map<String,String> data = remoteMessage.getData();
        String title = data.get("title");
        String message = data.get("message");

        //notification channel
        NotificationHelper helper;
        Notification.Builder builder;

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        helper = new NotificationHelper(this);
        builder = helper.getHawkerNotification(title,message,defaultSoundUri);

        helper.getManager().notify(new Random().nextInt(),builder.build());
    }

    private void updateTokenToFireBase(String token) {
        IHawkerAPI mService = Common.getAPI();
        mService.updateToken(Common.currentUser.getPhone(), token,"0")
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.d("DEBUG",response.toString());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("DEBUG",t.getMessage());
                    }
                });
    }
}
