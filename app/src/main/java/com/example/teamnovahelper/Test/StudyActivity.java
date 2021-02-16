package com.example.teamnovahelper.Test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.teamnovahelper.MainActivity.InformationActivity;
import com.example.teamnovahelper.MainActivity.RecordActivity;
import com.example.teamnovahelper.R;
import com.example.teamnovahelper.Service.StudyService;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class StudyActivity extends AppCompatActivity {
    //StudyActivity 는 오늘 공부 시간을 측정하고 기록하는 액티비티입니다.
    //10시간을 기준(목표)으로 공부 시간에 따라 프로그레스 바가 점점 채워집니다.
    private Button button_start, button_record;
    private TextView textView_time, textView_record;
    private ConstraintLayout constraint_layout;

    private String record = "";

    private long back_button_time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //기록한 순 공부시간을 불러오는 부분입니다.
        SharedPreferences sharedPreferences =  getSharedPreferences("Stop watch", MODE_PRIVATE);
        String saved_record = sharedPreferences.getString("stop_watch_record", "");

        setContentView(R.layout.activity_timer);
        setTitle("공부하기");

        //저장된 배경색을 불러오는 부분입니다.
        constraint_layout = (ConstraintLayout) findViewById(R.id.constraint_layout);
        sharedPreferences =  getSharedPreferences("saveColor", MODE_PRIVATE);
        int background_color = sharedPreferences.getInt("Background color", 0);
        constraint_layout.setBackgroundColor(background_color);

        button_start = (Button) findViewById(R.id.button_start);
        button_record = (Button) findViewById(R.id.button_record);

        textView_time = (TextView) findViewById(R.id.textView_time);
        textView_record = (TextView) findViewById(R.id.textView_record);
        textView_record.setText(saved_record);
        textView_time.setText("00시간 00분 00초");

        button_record.setVisibility(View.GONE);

        StudyActivity.AnimationThread animation_thread = new StudyActivity.AnimationThread();
        animation_thread.start();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Counter");

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Integer integerTime = intent.getIntExtra("TimeRemaining",0);

                int second = integerTime % 60;
                int minute = (integerTime / 60) % 60;
                int hour = integerTime / 3600;
                String result = String.format("%02d시간 %02d분 %02d초", hour,minute,second);
                textView_time.setText(result);

                ProgressBar progress_bar = (ProgressBar) findViewById(R.id.progressBar) ;
                progress_bar.setProgress(integerTime) ;
            }
        };

        registerReceiver(broadcastReceiver, intentFilter);

        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                button_record.setVisibility(View.VISIBLE);
                startService();
            }
        });
        button_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_start.setVisibility(View.VISIBLE);

                //기록하기 버튼을 누르면 시간을 찍어 출력합니다.
                record = String.valueOf(textView_time.getText());

                //여기에서 순 공부시간을 저장하고, 액티비티를 시작할 때 불러옵니다.
                SharedPreferences.Editor editor;
                SharedPreferences sharedPreferences = getSharedPreferences("Stop watch", MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString("stop_watch_record", record);
                editor.apply();

                textView_record.setText(record);
                textView_time.setText("00시간 00분 00초");
                unregisterReceiver(broadcastReceiver);
                stopService();
                button_record.setVisibility(View.GONE);

            }
        });

        //보다 정돈된 느낌을 주기 위해 TabLayout 을 사용하였습니다.
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab) ;
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition() ;
                changeView(position) ;
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        }) ;
    }

    Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message message) {
        }
    };

    class AnimationThread extends Thread {
        @Override
        public void run() {
            Resources resources = getResources();
            ImageView imageView = findViewById(R.id.imageView_study);

            ArrayList<Drawable> imageList = new ArrayList<Drawable>();
            //공부하는 이미지의 순서대로 1,2,3,4 번호를 매겼습니다.
            //어레이리스트에 추가된 순서대로 1초마다 다른이미지를 보여주는 쓰레드입니다.
            imageList.add(resources.getDrawable(R.drawable.study1));
            imageList.add(resources.getDrawable(R.drawable.study2));
            imageList.add(resources.getDrawable(R.drawable.study3));
            imageList.add(resources.getDrawable(R.drawable.study4));

            int second = 0;
            while(true) {

                second = second + 1;
                int rest = second % 4;
                final Drawable drawable = imageList.get(rest);

                handler.post(new Runnable() { //메세지가 아닌 실행 코드가 담긴 러너블 객체를 메세지큐에 보내는 작업
                    @Override
                    public void run() {
                        imageView.setImageDrawable(drawable);
                    }
                });

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return; // 인터럽트 받을 경우 return 됨 (쓰레드 종료)
                }
            }

        }
    }

    private void changeView(int index) {
        String Login_User_ID = getIntent().getStringExtra("Login_User_ID");
        //탭 레이아웃의 아이템들을 클릭했을 때 인텐트를 전달하는 메소드를 각각 입력하였습니다.
        switch (index) {
            case 0 :

                break ;
            case 1 :
                Intent information_intent = new Intent(StudyActivity.this, InformationActivity.class);
                information_intent.putExtra("Login_User_ID", Login_User_ID);
                StudyActivity.this.startActivity(information_intent);
                finish();
                break ;
            case 2 :
                Intent intent = new Intent(StudyActivity.this, RecordActivity.class);
                intent.putExtra("Login_User_ID", Login_User_ID);
                StudyActivity.this.startActivity(intent);
                finish();
                break ;

        }
    }
    public void startService() {

        Intent serviceIntent = new Intent(this, StudyService.class);
//        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");
        serviceIntent.putExtra("TimeValue", 0);
        startService(serviceIntent);
//        ContextCompat.startForegroundService(this, serviceIntent);
    }
    public void stopService() {
        Intent serviceIntent = new Intent(this, StudyService.class);
        stopService(serviceIntent);
    }
    @Override
    public void onBackPressed() {
        //뒤로가기를 한번 눌렀을 때 어플이 종료된다면,
        //유저가 뒤로가기를 잘못 눌렀을 때 불편할 수 있기 때문에 이 기능을 추가했습니다.
        long curTime = System.currentTimeMillis();
        long gapTime = curTime - back_button_time;

        if(0 <= gapTime && 2000 >= gapTime) {
            super.onBackPressed();
        }
        else {
            back_button_time = curTime;
            Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르면 앱이 종료됩니다.",Toast.LENGTH_SHORT).show();
        }
    }
}