package com.example.teamnovahelper.Test;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teamnovahelper.R;

public class TimerTestActivity extends AppCompatActivity {
    private Button button_start, button_record, button_pause;
    private TextView textView_time, textView_record;

    private Thread thread = null;
    private String record = "";
    private Boolean isRunning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_test);

        //findViewById
        button_start = (Button) findViewById(R.id.button_start);
        button_record = (Button) findViewById(R.id.button_record);
        button_pause = (Button) findViewById(R.id.button_pause);
        textView_time = (TextView) findViewById(R.id.textView_time);
        textView_record = (TextView) findViewById(R.id.textView_record);
        //INVISIBLE과 GONE의 차이는 INVISIBLE은 보이지 않고
        //어떤 이벤트도 동작하지 않지만 자리는 차지합니다.
        //GONE은 자리조차 차지하지 않고 아예 사라진 것처럼 됩니다.
        //STOP 버튼을 누르면 STOP버튼과 PAUSE버튼, RECORD 버튼이 사라지고 START버튼이 다시 생긴다
        button_record.setVisibility(View.INVISIBLE);
        button_pause.setVisibility(View.INVISIBLE);

        SharedPreferences sharedPreferences =  getSharedPreferences("Stop watch", MODE_PRIVATE);
        String saved_record = sharedPreferences.getString("stop_watch_record", "");

        textView_time.setText("00시간 00분 00초");
        textView_record.setText(saved_record);


        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // START 버튼을 누르면 START 버튼이 사라지고, 나머지 버튼이 보인다
                v.setVisibility(View.GONE);
                button_record.setVisibility(View.VISIBLE);
                button_pause.setVisibility(View.VISIBLE);
                //START 버튼을 누를 때마다 쓰레드 객체를 새로 만들어 시작한다
                thread = new Thread(new timeThread());
                thread.start();
            }
        });

        button_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //RECORD 버튼을 누르면 시간을 찍어 출력한다
                v.setVisibility(View.GONE);
                button_record.setVisibility(View.GONE);
                button_start.setVisibility(View.VISIBLE);
                button_pause.setVisibility(View.GONE);
                record = String.valueOf(textView_time.getText())/*+"\n"*/;

                SharedPreferences.Editor editor;
                SharedPreferences sharedPreferences = getSharedPreferences("Stop watch", MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString("stop_watch_record", record);
                editor.apply();

                textView_record.setText(record);
                thread.interrupt();
                textView_time.setText("00시간 00분 00초");
            }
        });

        button_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //일시정지를 위한 boolean 변수
                isRunning = !isRunning;
                if(isRunning){
                    button_pause.setText("일시정지");
                }else{
                    button_pause.setText("시작");
                }
            }
        });
    }

    Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {

            // 시간 format :
            int milliSecond = msg.arg1 % 100;
            int second = (msg.arg1 / 100) % 60;
            int minute = ((msg.arg1 /100) / 60) % 60;
            int hour = (msg.arg1 /100) / 3600;/*(msg.arg1 % 3600 ) % 24;*/
            String result = String.format("%02d시간 %02d분 %02d초", hour,minute,second);
            textView_time.setText(result);
        }
    };

    public class timeThread implements Runnable{
        @Override
        public void run() {

            int i = 0;

            while(true){
                while(isRunning){ //일시정지를 누르면 멈추도록
                    Message msg = new Message();
                    msg.arg1=i = i+1;
                    handler.sendMessage(msg);

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return; // 인터럽트 받을 경우 return됨
                    }
                }
            }
        }
    }
}