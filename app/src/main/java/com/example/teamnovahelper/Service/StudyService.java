package com.example.teamnovahelper.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.teamnovahelper.Test.StudyActivity;
import com.example.teamnovahelper.R;

import java.util.Timer;
import java.util.TimerTask;

public class StudyService extends Service {

    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private Thread Stop_watch_thread = null;
    private Boolean is_running = true;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Integer[] timeRemaining = {intent.getIntExtra("TimeValue", 0)};
          final Timer timer = new Timer();
          timer.scheduleAtFixedRate(new TimerTask() {
              @Override
              public void run() {
                  Intent intent_local = new Intent();
                  intent_local.setAction("Counter");

                   timeRemaining[0]++;

                   NotificationUpdate(timeRemaining[0]);

                  intent_local.putExtra("TimeRemaining",timeRemaining[0]);
                   sendBroadcast(intent_local);
              }
          },0,1000);

        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
//    private void createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel serviceChannel = new NotificationChannel(
//                    CHANNEL_ID,
//                    "Foreground Service Channel",
//                    NotificationManager.IMPORTANCE_DEFAULT
//            );
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(serviceChannel);
//        }
//    }
    public void NotificationUpdate(Integer time){
        int second = time % 60;
        int minute = (time / 60) % 60;
        int hour = time / 3600;
        String result = String.format("%02d시간 %02d분 %02d초", hour,minute,second);
        try{
            Intent notificationIntent = new Intent(this, StudyActivity.class);
            final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,0);

            final Notification[] notification = {new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("공부하기")
                    .setContentText("공부한 시간 : "+result)
                    .setSmallIcon(R.drawable.ic_study)
                    .setContentIntent(pendingIntent)
                    .build()};

            startForeground(1, notification[0]);

            NotificationChannel notificationChannel = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationChannel = new NotificationChannel(CHANNEL_ID, "My counter Service", NotificationManager.IMPORTANCE_LOW);
            }
            NotificationManager notificationManager = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notificationManager = getSystemService(NotificationManager.class);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }catch (Exception e){
                    e.printStackTrace();
        }


    }
}
