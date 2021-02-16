package com.example.teamnovahelper.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.teamnovahelper.R;
import com.example.teamnovahelper.MainActivity.StopWatchActivity;

import static com.example.teamnovahelper.MainActivity.StopWatchActivity.progress_bar;

public class StudyForegroundService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private Thread Stop_watch_thread;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "서비스 시작",Toast.LENGTH_SHORT).show();
        Stop_watch_thread = new Thread(new timeThread());
        Stop_watch_thread.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
            Stop_watch_thread.interrupt();
            Toast.makeText(getApplicationContext(), "서비스 종료",Toast.LENGTH_SHORT).show();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public class timeThread implements Runnable{
        @Override
        public void run() {

            int second = 0;

            while(true){
                // 1초마다 arg1 값이 1증가하는 메세지를 핸들러가 메세지큐에 보내는 작업입니다.
                while(StopWatchActivity.is_running){ //일시정지를 누르면 멈추도록 구현했습니다.
                    Message time_message = new Message();
                    time_message.arg1=second = second+1;
                    handler.sendMessage(time_message);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return; // 인터럽트 받을 경우 return 됨 (쓰레드 종료)
                    }
                }
            }
        }
    }
     Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message message) {
            // handleMessage 메소드에서는 메세지로부터 전달받은 데이터를 화면에 어떻게 보여줄 것인가를 입력하는 곳입니다.
            // 그래서 message.arg1 가 변함에 따라 textView_time 과 progress_bar 가 변하는 메소드를 입력했습니다.
            int second = message.arg1 % 60;
            int minute = (message.arg1 / 60) % 60;
            int hour = message.arg1 / 3600;

            progress_bar.setProgress(second) ;
            String result = String.format("%02d시간 %02d분 %02d초", hour,minute,second);
            StopWatchActivity.textView_time.setText(result);
            NotificationUpdate(result);

        }
    };

    public void NotificationUpdate(String result){
        try{
            Intent notificationIntent = new Intent(this, StopWatchActivity.class);
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
