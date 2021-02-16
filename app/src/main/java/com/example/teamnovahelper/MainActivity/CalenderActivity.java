package com.example.teamnovahelper.MainActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.teamnovahelper.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class CalenderActivity extends AppCompatActivity {
    //https://cpcp127.tistory.com/21 참고 블로그

    public String saveFileName =null;
    public String stringTodo =null;
    public CalendarView calendarView;
    public Button buttonChangeTodo, buttonDeleteTodo, buttonSaveTodo;
    public TextView textViewDiary, TextViewTodo, textViewCalenderTitle, textViewCalenderExplain;
    public EditText editTextTodo;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        //저장한 배경색을 나타내는 부분입니다.
        ConstraintLayout constraint_layout = (ConstraintLayout) findViewById(R.id.calendar_layout);
        SharedPreferences layout_sharedPreferences =  getSharedPreferences("saveColor", MODE_PRIVATE);
        int background_color = layout_sharedPreferences.getInt("Background color", 0);
        constraint_layout.setBackgroundColor(background_color);

        calendarView=findViewById(R.id.calendarView);
        textViewDiary =findViewById(R.id.textView_diary);
        buttonSaveTodo =findViewById(R.id.button_save);
        buttonDeleteTodo =findViewById(R.id.button_delete);
        buttonChangeTodo =findViewById(R.id.button_change);
        TextViewTodo =findViewById(R.id.textView_todo);
        textViewCalenderTitle =findViewById(R.id.textView_calender_title);
        editTextTodo =findViewById(R.id.editText_todo);
        textViewCalenderExplain = findViewById(R.id.textView_calender_explain);

        //로그인 및 회원가입 엑티비티에서 이름을 받아옴
        String Login_User_ID = getIntent().getStringExtra("Login_User_ID");

        SharedPreferences mSharedPreferences =  getSharedPreferences("saveID", MODE_PRIVATE);
        String User_information = mSharedPreferences.getString(Login_User_ID, "");
        String[] User = User_information.split(",");

        textViewCalenderTitle.setText(User[1]+"님의 달력 일기장");


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                    textViewDiary.setVisibility(View.VISIBLE);
                    buttonSaveTodo.setVisibility(View.VISIBLE);
                    editTextTodo.setVisibility(View.VISIBLE);
                    TextViewTodo.setVisibility(View.INVISIBLE);
                    buttonChangeTodo.setVisibility(View.INVISIBLE);
                    buttonDeleteTodo.setVisibility(View.INVISIBLE);
                    textViewCalenderExplain.setVisibility(View.INVISIBLE);
                    textViewDiary.setText(String.format("%d 년 %d 월 %d 일 할 일",year,month+1,dayOfMonth));
                    editTextTodo.setText("");
                    checkDay(year,month,dayOfMonth,Login_User_ID);
            }
        });

        buttonSaveTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDiary(saveFileName);
                stringTodo = editTextTodo.getText().toString();
                TextViewTodo.setText(stringTodo);
                buttonSaveTodo.setVisibility(View.INVISIBLE);
                buttonChangeTodo.setVisibility(View.VISIBLE);
                buttonDeleteTodo.setVisibility(View.VISIBLE);
                editTextTodo.setVisibility(View.INVISIBLE);
                TextViewTodo.setVisibility(View.VISIBLE);

            }
        });
    }

    public void  checkDay(int cYear,int cMonth,int cDay,String userID){
        saveFileName =""+userID+cYear+"-"+(cMonth+1)+""+"-"+cDay+".txt";//저장할 파일 이름설정
        FileInputStream fileInputStream=null;//FileStream fis 변수

        try{
            fileInputStream=openFileInput(saveFileName);

            byte[] fileData=new byte[fileInputStream.available()];
            fileInputStream.read(fileData);
            fileInputStream.close();

            stringTodo =new String(fileData);

                editTextTodo.setVisibility(View.INVISIBLE);
                TextViewTodo.setVisibility(View.VISIBLE);
                TextViewTodo.setText(stringTodo);

                buttonSaveTodo.setVisibility(View.INVISIBLE);
                buttonChangeTodo.setVisibility(View.VISIBLE);
                buttonDeleteTodo.setVisibility(View.VISIBLE);

            buttonChangeTodo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editTextTodo.setVisibility(View.VISIBLE);
                    TextViewTodo.setVisibility(View.INVISIBLE);
                    editTextTodo.setText(stringTodo);

                    buttonSaveTodo.setVisibility(View.VISIBLE);
                    buttonChangeTodo.setVisibility(View.INVISIBLE);
                    buttonDeleteTodo.setVisibility(View.INVISIBLE);
                    TextViewTodo.setText(editTextTodo.getText());
                }

            });
            buttonDeleteTodo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextViewTodo.setVisibility(View.INVISIBLE);
                    editTextTodo.setText("");
                    editTextTodo.setVisibility(View.VISIBLE);
                    buttonSaveTodo.setVisibility(View.VISIBLE);
                    buttonChangeTodo.setVisibility(View.INVISIBLE);
                    buttonDeleteTodo.setVisibility(View.INVISIBLE);
                    removeDiary(saveFileName);
                }
            });
            if(TextViewTodo.getText()==null){
                TextViewTodo.setVisibility(View.INVISIBLE);
                textViewDiary.setVisibility(View.VISIBLE);
                buttonSaveTodo.setVisibility(View.VISIBLE);
                buttonChangeTodo.setVisibility(View.INVISIBLE);
                buttonDeleteTodo.setVisibility(View.INVISIBLE);
                editTextTodo.setVisibility(View.VISIBLE);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @SuppressLint("WrongConstant")
    public void removeDiary(String readDay){
        FileOutputStream fileOutputStream=null;
        try{
            fileOutputStream=openFileOutput(readDay,MODE_NO_LOCALIZED_COLLATORS);
            fileOutputStream.write(null);
            fileOutputStream.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @SuppressLint("WrongConstant")
    public void saveDiary(String readDay){
        FileOutputStream fileOutputStream=null;
        try{
            fileOutputStream=openFileOutput(readDay,MODE_NO_LOCALIZED_COLLATORS);
            String content= editTextTodo.getText().toString();
            fileOutputStream.write((content).getBytes());
            fileOutputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}