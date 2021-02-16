package com.example.teamnovahelper.MainActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.teamnovahelper.R;
import com.example.teamnovahelper.Test.CalendarTestActivity;
import com.google.android.material.tabs.TabLayout;

public class RecordActivity extends AppCompatActivity {
    //RecordActivity 는 오늘 할 일, 출퇴근 기록, 피드백 기록 등 팀노바에서 공부하면서 필요한 기능들을
    //담은 액티비티로 이동할 수 있는 액티비티입니다. 설정창을 눌러 설정 화면으로 갈 수도 있고, 하단 탭 레이아웃을 통해
    //StudyActivity 나 InformationActivity 로 갈 수도 있습니다.
    private Button To_Do_List;
    private Button check_attendance;
    private Button Feedback;
    private ConstraintLayout constraint_layout;

    private long back_button_time = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_actions, menu) ;
        return true ;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings :
                String Login_User_ID = getIntent().getStringExtra("Login_User_ID");

                Intent intent = new Intent(RecordActivity.this, SettingActivity.class);
                intent.putExtra("Login_User_ID", Login_User_ID);
                RecordActivity.this.startActivity(intent);
                return true ;
            default :
                return super.onOptionsItemSelected(item) ;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        setTitle("기록하기");
        constraint_layout = (ConstraintLayout) findViewById(R.id.constraint_layout);
        //저장한 배경색을 나타내는 부분입니다.
        SharedPreferences sharedPreferences =  getSharedPreferences("saveColor", MODE_PRIVATE);
        int background_color = sharedPreferences.getInt("Background color", 0);
        constraint_layout.setBackgroundColor(background_color);
        //자동로그인을 위한 유저 아이디를 인식하는 부분입니다.
        String Login_User_ID = getIntent().getStringExtra("Login_User_ID");
        SharedPreferences.Editor editor;
        sharedPreferences = getSharedPreferences("AutoLogIn", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("User_ID", Login_User_ID);
        editor.apply();

        To_Do_List = findViewById(R.id.ImageView_GeekNews); //액티비티 안에 To_Do_List 라는 id를 가진 객체를 찾는다
        To_Do_List.setOnClickListener(new View.OnClickListener() { //To_Do_List 를 클릭했을 때 onClick 메소드를 실행한다
            @Override
            public void onClick(View v) { //버튼을 클릭했을 때 실행하는 메소드
                // intent 라는 메세지 역할을 하는 객체를 생성해서 RecordActivity 에서 CalenderActivity 로 정보를 전달한다.
                Intent intent = new Intent(RecordActivity.this, CalendarTestActivity.class);
                intent.putExtra("Login_User_ID", Login_User_ID);
                RecordActivity.this.startActivity(intent); //intent 객체로 MainActivity 에서 To_Do_List_Activity 로 이동하라는 정보를 전달함
            }
        });
        check_attendance = findViewById(R.id.check_attendance);
        check_attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RecordActivity.this, CheckOutActivity.class);
                intent.putExtra("Login_User_ID", Login_User_ID);
                RecordActivity.this.startActivity(intent);
            }
        });
        Feedback = findViewById(R.id.Feedback);
        Feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RecordActivity.this, FeedbackActivity.class);
                intent.putExtra("Login_User_ID", Login_User_ID);
                RecordActivity.this.startActivity(intent);
            }
        });

        //TabLayout 을 하단에 배치하여 유저가 어플의 주요 기능을 알아보기 쉽고,
        // 이용이 편리하도록 하였습니다.
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

    private void changeView(int index) {
        String Login_User_ID = getIntent().getStringExtra("Login_User_ID");
        switch (index) {

            case 0 :

                break ;
            case 1 :
                Intent stop_watch_intent = new Intent(RecordActivity.this, StopWatchActivity.class);
                stop_watch_intent.putExtra("Login_User_ID", Login_User_ID);
                RecordActivity.this.startActivity(stop_watch_intent);
                finish();
                break ;
            case 2 :
                Intent information_intent = new Intent(RecordActivity.this, InformationActivity.class);
                information_intent.putExtra("Login_User_ID", Login_User_ID);
                RecordActivity.this.startActivity(information_intent);
                finish();
                break ;
            case 3 :
                Intent map_intent = new Intent(RecordActivity.this, MapActivity.class);
                map_intent.putExtra("Login_User_ID", Login_User_ID);
                RecordActivity.this.startActivity(map_intent);
                finish();
                break ;
        }
    }
    @Override
    protected void onRestart() {
        //배경색이 바뀌는 상황을 좀 더 매끄럽게 표현하기 위해 onRestart() 안에 기능을 구현하였습니다.
        super.onRestart();
        SharedPreferences sharedPreferences =  getSharedPreferences("saveColor", MODE_PRIVATE);
        int background_color = sharedPreferences.getInt("Background color", 0);
        constraint_layout.setBackgroundColor(background_color);
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