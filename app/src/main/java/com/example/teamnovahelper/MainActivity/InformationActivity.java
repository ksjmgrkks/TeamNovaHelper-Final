package com.example.teamnovahelper.MainActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.teamnovahelper.R;
import com.google.android.material.tabs.TabLayout;

public class InformationActivity extends AppCompatActivity {
    //informationActivity 는 매일 코로나 확진 현황을 확인해 나가서 공부할지 집에서 공부할지 판단하는 데 도움을 주고,
    //it 소식들을 접할 수 있는 좋은 사이트들을 암시적 인텐트를 통해 연결해놓은 액티비티입니다.

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

                Intent intent = new Intent(InformationActivity.this, SettingActivity.class);
                intent.putExtra("Login_User_ID", Login_User_ID);
                InformationActivity.this.startActivity(intent);
                return true ;
            default :
                return super.onOptionsItemSelected(item) ;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        setTitle("코로나/IT 정보");
        constraint_layout = (ConstraintLayout) findViewById(R.id.constraint_layout);

        SharedPreferences sharedPreferences =  getSharedPreferences("saveColor", MODE_PRIVATE);
        int background_color = sharedPreferences.getInt("Background color", 0);
        constraint_layout.setBackgroundColor(background_color);


        ImageView imageViewCOVID = findViewById(R.id.ImageView_COVID);
        imageViewCOVID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InformationActivity.this, COVID19Activity.class);
                InformationActivity.this.startActivity(intent);
            }
        });
        ImageView imageViewGeekNews = findViewById(R.id.ImageView_GeekNews);
        imageViewGeekNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent implicit_intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://news.hada.io/"));
                startActivity(implicit_intent);
            }
        });
        ImageView imageViewBloter = findViewById(R.id.ImageView_Bloter);
        imageViewBloter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent implicit_intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.bloter.net/"));
                startActivity(implicit_intent);
            }
        });
        ImageView imageViewEtnews = findViewById(R.id.ImageView_Etnews);
        imageViewEtnews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent implicit_intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.etnews.com/"));
                startActivity(implicit_intent);
            }
        });
        ImageView imageViewZdnet = findViewById(R.id.ImageView_Zdnet);
        imageViewZdnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent implicit_intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://zdnet.co.kr/"));
                startActivity(implicit_intent);
            }
        });

        ImageView ImageViewPublic = findViewById(R.id.ImageView_Public);
        ImageViewPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent implicit_intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://teamnova.co.kr/index2.php"));
                startActivity(implicit_intent);
            }
        });

        ImageView ImageViewMember = findViewById(R.id.ImageView_Member);
        ImageViewMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent implicit_intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.teamnovamember.co.kr/index.php"));
                startActivity(implicit_intent);
            }
        });

        //TabLayout 을 하단에 배치하여 유저가 어플의 주요 기능을 알아보기 쉽고,
        // 이용이 편리하도록 하였습니다.
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab) ;
        tabLayout.selectTab(tabLayout.getTabAt(2));
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
                Intent record_intent = new Intent(InformationActivity.this, RecordActivity.class);
                record_intent.putExtra("Login_User_ID", Login_User_ID);
                InformationActivity.this.startActivity(record_intent);
                finish();
                break ;
            case 1 :
                Intent study_intent = new Intent(InformationActivity.this, StopWatchActivity.class);
                study_intent.putExtra("Login_User_ID", Login_User_ID);
                InformationActivity.this.startActivity(study_intent);
                finish();
                break ;
            case 2 :

                break ;
            case 3 :
                Intent map_intent = new Intent(InformationActivity.this, MapActivity.class);
                map_intent.putExtra("Login_User_ID", Login_User_ID);
                InformationActivity.this.startActivity(map_intent);
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