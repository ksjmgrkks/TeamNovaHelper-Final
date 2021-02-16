package com.example.teamnovahelper.MainActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.teamnovahelper.R;

import java.io.FileOutputStream;

public class TodoEditActivity extends AppCompatActivity {
    EditText EditTextContents;
    String saveFileName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_edit);

        //저장한 배경색을 나타내는 부분입니다.
        ConstraintLayout constraint_layout = (ConstraintLayout) findViewById(R.id.layout);
        SharedPreferences sharedPreferences =  getSharedPreferences("saveColor", MODE_PRIVATE);
        int background_color = sharedPreferences.getInt("Background color", 0);
        constraint_layout.setBackgroundColor(background_color);

        String Login_User_ID = getIntent().getStringExtra("Login_User_ID");
        String date = getIntent().getStringExtra("Date");
        String contents = getIntent().getStringExtra("Contents");

        EditTextContents = findViewById(R.id.editText_contents);
        TextView TextViewDate = findViewById(R.id.textView_date);
        Button ButtonAdd = findViewById(R.id.button_add);
        EditTextContents.setText(contents);
        TextViewDate.setText(date);
        ButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveFileName =""+Login_User_ID+date+".txt";
                saveDiary(saveFileName);
                String contents = EditTextContents.getText().toString();
                Intent intent = new Intent();//startActivity()를 할것이 아니므로 그냥 빈 인텐트로 만듦
                intent.putExtra("contents",contents);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    @SuppressLint("WrongConstant")
    public void saveDiary(String fileName){
        FileOutputStream fos=null;
        try{
            fos=openFileOutput(fileName,MODE_NO_LOCALIZED_COLLATORS);
            String content = EditTextContents.getText().toString();
            fos.write((content).getBytes());
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}