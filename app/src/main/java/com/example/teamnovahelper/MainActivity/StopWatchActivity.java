package com.example.teamnovahelper.MainActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.teamnovahelper.R;
import com.example.teamnovahelper.Service.StudyForegroundService;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;

public class StopWatchActivity extends AppCompatActivity {
    private Button button_start, button_record, button_pause;
    public static TextView textView_time;
    public static ProgressBar progress_bar;

    private ConstraintLayout constraint_layout;
    private long back_button_time = 0;
    private String record = "";
    public static Boolean is_running = true;

    Calendar cal = Calendar.getInstance();
    //현재 년도, 월, 일
    int year = cal.get (Calendar.YEAR);
    int month = cal.get (Calendar.MONTH);
    int date = cal.get (Calendar.DATE) ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //기록한 순 공부시간을 불러오는 부분입니다.
        SharedPreferences sharedPreferences;

        setContentView(R.layout.activity_stop_watch);
        setTitle("공부하기");

        //저장된 배경색을 불러오는 부분입니다.
        constraint_layout = (ConstraintLayout) findViewById(R.id.constraint_layout);
        sharedPreferences =  getSharedPreferences("saveColor", MODE_PRIVATE);
        int background_color = sharedPreferences.getInt("Background color", 0);
        constraint_layout.setBackgroundColor(background_color);

        button_start = (Button) findViewById(R.id.button_start);
        button_record = (Button) findViewById(R.id.button_record);
//        button_pause = (Button) findViewById(R.id.button_pause);
        progress_bar = (ProgressBar) findViewById(R.id.progressBar) ;
        textView_time = (TextView) findViewById(R.id.textView_time);
        textView_time.setText("00시간 00분 00초");
        //INVISIBLE 과 GONE 의 차이는 INVISIBLE 은 보이지 않고
        //어떤 이벤트도 동작하지 않지만 자리는 차지합니다. GONE 은 자리조차 차지하지 않고 아예 사라진 것처럼 됩니다.
        //'기록' 버튼을 누르면 '기록'버튼과 '일시정지'버튼이 사라지고 '시작'버튼이 다시 생깁니다.
        //버튼이 다 보이면, 조작할 때 불편할 수 있기 때문에, 필요한 버튼만 보이도록 했습니다.

        SharedPreferences mSharedPreferences =  getSharedPreferences("saveState", MODE_PRIVATE);
        int state = mSharedPreferences.getInt("State", 0);
        if(state == 1){
            button_start.setVisibility(View.GONE);
            button_record.setVisibility(View.VISIBLE);
//            button_pause.setVisibility(View.VISIBLE);
        }
        else{
            button_record.setVisibility(View.GONE);
            button_start.setVisibility(View.VISIBLE);
//            button_pause.setVisibility(View.GONE);
        }

        AnimationThread animation_thread = new StopWatchActivity.AnimationThread();
        animation_thread.start();

        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                button_record.setVisibility(View.VISIBLE);
//                button_pause.setVisibility(View.VISIBLE);

                SharedPreferences.Editor mEditor;
                SharedPreferences mSharedPreferences = getSharedPreferences("saveState", MODE_PRIVATE);
                mEditor = mSharedPreferences.edit();
                mEditor.putInt("State", 1);
                mEditor.apply();

                startService();
            }
        });
        button_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record = String.valueOf(textView_time.getText())/*+"\n"*/;
                AlertDialog.Builder builder = new AlertDialog.Builder(StopWatchActivity.this);
                builder.setTitle("공부시간을 기록하시겠습니까?");
                builder.setMessage("공부 시간 : "+record+"\n" +
                        "기록 날짜 : "+year+"년"+(month+1)+"월"+date+"일");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //기록하기 버튼을 누르면 시간을 찍어 출력합니다.
                        v.setVisibility(View.GONE);
                        button_record.setVisibility(View.GONE);
                        button_start.setVisibility(View.VISIBLE);
                        // button_pause.setVisibility(View.GONE);


                        SharedPreferences.Editor mEditor;
                        SharedPreferences mSharedPreferences = getSharedPreferences("saveState", MODE_PRIVATE);
                        mEditor = mSharedPreferences.edit();
                        mEditor.putInt("State", 0);
                        mEditor.apply();

                        stopService();
                        textView_time.setText("00시간 00분 00초");
                    }
                });
                builder.setNegativeButton("아니요",null);
                builder.create().show();
            }
        });

        Button button = (Button) findViewById(R.id.button_graph);

        button.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent mIntent = new Intent(getApplicationContext(), GraphActivity.class);
              startActivity(mIntent);
          }
        });

//        button_pause.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //일시정지를 위한 boolean 변수
//                is_running = !is_running;
//                if(is_running){
//                    button_pause.setText("일시정지");
//                }else{
//                    button_pause.setText("시작");
//                }
//            }
//        });

        //TabLayout 을 하단에 배치하여 유저가 어플의 주요 기능을 알아보기 쉽고,
        // 이용이 편리하도록 하였습니다.
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab) ;
        tabLayout.selectTab(tabLayout.getTabAt(1));
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
    private void changeView(int index) {
        String Login_User_ID = getIntent().getStringExtra("Login_User_ID");
        switch (index) {
            case 0 :
                Intent intent = new Intent(StopWatchActivity.this, RecordActivity.class);
                intent.putExtra("Login_User_ID", Login_User_ID);
                StopWatchActivity.this.startActivity(intent);
                finish();
                break ;
            case 1 :

                break ;
            case 2 :
                Intent information_intent = new Intent(StopWatchActivity.this, InformationActivity.class);
                information_intent.putExtra("Login_User_ID", Login_User_ID);
                StopWatchActivity.this.startActivity(information_intent);
                finish();
                break ;
            case 3 :
                Intent map_intent = new Intent(StopWatchActivity.this, MapActivity.class);
                map_intent.putExtra("Login_User_ID", Login_User_ID);
                StopWatchActivity.this.startActivity(map_intent);
                finish();
                break ;
        }
    }
    static Handler handler = new Handler(Looper.getMainLooper()){
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

    public void startService() {
        startService(new Intent(getApplicationContext(), StudyForegroundService.class));
    }
    public void stopService() {
        stopService(new Intent(getApplicationContext(), StudyForegroundService.class));
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