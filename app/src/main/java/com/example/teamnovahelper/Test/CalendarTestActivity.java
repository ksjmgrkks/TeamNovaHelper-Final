package com.example.teamnovahelper.Test;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.teamnovahelper.Database.DotDictionary;
import com.example.teamnovahelper.Decorator.EventDecorator;
import com.example.teamnovahelper.MainActivity.TodoEditActivity;
import com.example.teamnovahelper.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;


public class CalendarTestActivity extends AppCompatActivity {
    String saveFileName;
    String stringTodo;
    MaterialCalendarView materialCalendarView;
    TextView TextViewContents;
    ImageView ImageViewDelete;
    ArrayList<DotDictionary> DotArrayList = new ArrayList<>();
    DotDictionary dict;
    String Login_User_ID;

    private static final String Dot_JSON = "Dot_json";
    private static final String Dot = "Dot_item";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender_test);

        //저장한 배경색을 나타내는 부분입니다.
        ConstraintLayout constraint_layout = (ConstraintLayout) findViewById(R.id.calendar_layout);
        SharedPreferences sharedPreferences =  getSharedPreferences("saveColor", MODE_PRIVATE);
        int background_color = sharedPreferences.getInt("Background color", 0);
        constraint_layout.setBackgroundColor(background_color);

        Login_User_ID = getIntent().getStringExtra("Login_User_ID");
        materialCalendarView = (MaterialCalendarView)findViewById(R.id.calendarView);

        materialCalendarView.state().edit()
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        //SharedPreferences 에서 저장한 json 불러오기 -> 어레이 리스트 -> 달력에 점 찍는 것 표현하기
        SharedPreferences preferences = getSharedPreferences(Dot,MODE_PRIVATE);
        String json = preferences.getString(Dot_JSON, "");
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < jsonArray.length() ; i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int year = jsonObject.getInt("dotYear");
                int month = jsonObject.getInt("dotMonth");
                int day = jsonObject.getInt("dotDay");
                DotDictionary dict = new DotDictionary(year,month,day);
                DotArrayList.add(dict);
                materialCalendarView.addDecorators(new EventDecorator(Color.RED, Collections.singleton(CalendarDay.from(year,month,day))));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                CardView cardView = findViewById(R.id.cardview);
                TextView TextViewDate = findViewById(R.id.date_todo);
                TextViewContents = findViewById(R.id.contents_todo);
                ImageViewDelete = findViewById(R.id.imageView_delete);
                cardView.setVisibility(View.VISIBLE);
                Button buttonDotDelete = findViewById(R.id.button_dot_delete);

                int Year = date.getYear();
                int Month = date.getMonth();
                int Day = date.getDay();
                Log.d("디버그태그", String.format("%s%d%d", Year, Month, Day));

                EventDecorator eventDecorator = new EventDecorator(Color.RED, Collections.singleton(CalendarDay.from(Year, Month, Day)));

                checkDay(Year,Month,Day,Login_User_ID);
                TextViewDate.setText(String.format("%d년 %d월 %d일",Year,Month,Day));

                buttonDotDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialCalendarView.removeDecorators();
                        DotArrayList.clear();
                    }
                });

                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(CalendarTestActivity.this, TodoEditActivity.class);
                        intent.putExtra("Login_User_ID", Login_User_ID);
                        intent.putExtra("Date", String.format("%d년 %d월 %d일",Year,Month,Day));
                        intent.putExtra("Contents", TextViewContents.getText().toString());
                        startActivityForResult(intent, 1);
                        materialCalendarView.addDecorators(eventDecorator);
                        dict = new DotDictionary(Year, Month, Day);
                        DotArrayList.add(dict); //첫번째 줄에 삽입됨
                    }
                });

                ImageViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CalendarTestActivity.this);
                builder.setTitle(String.format("%d년 %d월 %d일",Year,Month,Day)+" \n일기의 내용을 삭제하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), "삭제 완료",Toast.LENGTH_SHORT).show();
                            removeDiary(""+Login_User_ID+String.format("%d년 %d월 %d일",Year,Month,Day)+".txt");
                            TextViewContents.setText("");
                    }
                });
                builder.setNegativeButton("아니요",null);
                builder.create().show();
                    }
                });
            }

        });
    }

    @SuppressLint("DefaultLocale")
    public void  checkDay(int Year, int Month, int Day, String userID){
        stringTodo = null;
        saveFileName =""+userID+String.format("%d년 %d월 %d일",Year,Month,Day)+".txt";//파일 이름설정
        FileInputStream fileInputStream=null;//FileStream fis 변수
        try{
            fileInputStream=openFileInput(saveFileName);
            byte[] fileData=new byte[fileInputStream.available()];
            fileInputStream.read(fileData);
            fileInputStream.close();

            stringTodo =new String(fileData);
            Log.d("스트링 들어오는값", stringTodo);
            TextViewContents.setText(stringTodo);
            Log.d("텍스트뷰 들어오는값", stringTodo);
        }catch (Exception e){
            e.printStackTrace();
            TextViewContents.setText("");
        }
    }
    @SuppressLint("WrongConstant")
    public void removeDiary(String fileName){
        FileOutputStream fos=null;
        try{
            fos=openFileOutput(fileName,MODE_NO_LOCALIZED_COLLATORS);
            String content="";
            fos.write((content).getBytes());
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences preferences = getSharedPreferences(Dot, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i <DotArrayList.size() ; i++){
                JSONObject Object = new JSONObject();//배열 내에 들어갈 json
                Object.put("dotYear",DotArrayList.get(i).getDotYear());
                Object.put("dotMonth",DotArrayList.get(i).getDotMonth());
                Object.put("dotDay",DotArrayList.get(i).getDotDay());
                jsonArray.put(Object);
            }
            editor.putString(Dot_JSON, jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String contents = data.getStringExtra("contents");
            TextViewContents.setText(contents);
        }
    }

}