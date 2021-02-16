package com.example.teamnovahelper.MainActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teamnovahelper.R;

public class SignUpActivity extends AppCompatActivity {
    //SignUpTestActivity 는 아이디, 비밀번호, 이름을 입력해 회원가입을 진행하는 액티비티입니다.

    private EditText edittext_id;
    private EditText edittext_password;
    private EditText edittext_password_confirm;
    private EditText edittext_name;
    private Button button_signup;

    //참고 유튜브 : https://www.youtube.com/watch?v=EPAiu_2R5P0
    private void DoJoin() {

        String Login_id = edittext_id.getText().toString().trim();
        String Login_password = edittext_password.getText().toString().trim();
        String Login_password_confirm = edittext_password_confirm.getText().toString().trim();
        String name = edittext_name.getText().toString().trim();

        if (Login_id.length() == 0){
            Toast.makeText(getApplicationContext(), "아이디를 입력해주세요",Toast.LENGTH_SHORT).show();
            edittext_id.requestFocus();
            return;
        }
        if (Login_password.length() == 0){
            Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요",Toast.LENGTH_SHORT).show();
            edittext_password.requestFocus();
            return;
        }
        if (name.length() == 0){
            Toast.makeText(getApplicationContext(), "이름을 입력해주세요",Toast.LENGTH_SHORT).show();
            edittext_name.requestFocus();
            return;
        }
        if (!Login_password.equals(Login_password_confirm)){
            Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.",Toast.LENGTH_SHORT).show();
            edittext_password_confirm.requestFocus();
            return;
        }
        SharedPreferences mSharedPreferences =  getSharedPreferences("saveID", MODE_PRIVATE);
        String ID_check = mSharedPreferences.getString(Login_id, "");
        if(!ID_check.equals("")){
            Toast.makeText(getApplicationContext(), "이미 사용중인 아이디입니다",Toast.LENGTH_SHORT).show();
            edittext_id.requestFocus();
            return;
        }
        String User_information = Login_password+","+name;
        SharedPreferences.Editor mEditor;
        mSharedPreferences = getSharedPreferences("saveID", MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mEditor.putString(Login_id, User_information);
        mEditor.apply();
        Toast.makeText(getApplicationContext(), "가입 성공!",Toast.LENGTH_SHORT).show();
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("회원가입");
        edittext_id = findViewById(R.id.edittext_id);
        edittext_password = findViewById(R.id.edittext_password);
        edittext_password_confirm = findViewById(R.id.edittext_password_confirm);
        edittext_name = findViewById(R.id.edittext_name);
        button_signup = findViewById(R.id.button_signup);
        button_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoJoin();
            }
        });
    }

}