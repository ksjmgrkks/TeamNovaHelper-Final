package com.example.teamnovahelper.MainActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teamnovahelper.R;

public class LogInActivity extends AppCompatActivity {
    //LogInTestActivity 는 로그인 기능을 담은 액티비티입니다.
    //SignUpTestActivity 에서 SharedPreferences 에 저장한 정보를 확인해 로그인을 진행합니다.

    private EditText edittext_id;
    private EditText edittext_password;
    private Button button_log_in;
    private Button button_sign_up;

    // 참고 영상 https://www.youtube.com/watch?v=HczySFrGqdQ
    // 유저에 따라 저장하는 내용이 다를 수 있기 때문에 회원가입 로그인 기능을 구현하였습니다.
    private void DoLogin() {

        String Login_id = edittext_id.getText().toString().trim();
        String Login_password = edittext_password.getText().toString().trim();

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
        SharedPreferences mSharedPreferences =  getSharedPreferences("saveID", MODE_PRIVATE);
        String User_information = mSharedPreferences.getString(Login_id, "");
        String[] User = User_information.split(",");
        if(User_information == ""){
            Toast.makeText(getApplicationContext(), "존재하지 않는 아이디입니다",Toast.LENGTH_SHORT).show();
            Log.d("디버그태그", "");
            edittext_id.requestFocus();
            return;
        } else if (!User[0].equals(Login_password)){
            Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다",Toast.LENGTH_SHORT).show();
            edittext_password.requestFocus();
            return;
        }

        Intent intent = new Intent(LogInActivity.this, RecordActivity.class);
        intent.putExtra("Login_User_ID", Login_id);
        LogInActivity.this.startActivity(intent);
        finish();
        Toast.makeText(getApplicationContext(), "로그인 성공",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        setTitle("로그인");

        edittext_id = findViewById(R.id.edittext_id);
        edittext_password = findViewById(R.id.edittext_password);

        SharedPreferences sharedPreferences =  getSharedPreferences("saveAutoLogIn", MODE_PRIVATE);
        Boolean auto_log_in = sharedPreferences.getBoolean("Auto_Log_in", false);
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox_autoLogIn) ;
        checkBox.setChecked(auto_log_in) ;

        checkBox = (CheckBox) findViewById(R.id.checkBox_autoLogIn) ;
        if (checkBox.isChecked()) {
            // TODO : CheckBox is checked.
            sharedPreferences =  getSharedPreferences("AutoLogIn", MODE_PRIVATE);
            String Login_User_ID = sharedPreferences.getString("User_ID", "");

            Intent intent = new Intent(LogInActivity.this, RecordActivity.class);
            intent.putExtra("Login_User_ID", Login_User_ID);
            LogInActivity.this.startActivity(intent);
            finish();
            Toast.makeText(getApplicationContext(), "자동 로그인 성공",Toast.LENGTH_SHORT).show();
        } else {
            // TODO : CheckBox is unchecked.
        }
        checkBox.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    // TODO : CheckBox is checked.
                    SharedPreferences.Editor editor;
                    SharedPreferences sharedPreferences = getSharedPreferences("saveAutoLogIn", MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("Auto_Log_in", true);
                    editor.apply();

                } else {
                    // TODO : CheckBox is unchecked.
                    SharedPreferences.Editor editor;
                    SharedPreferences sharedPreferences = getSharedPreferences("saveAutoLogIn", MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("Auto_Log_in", false);
                    editor.apply();
                }
            }
        }) ;
        button_log_in = findViewById(R.id.button_log_in);
        button_log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoLogin();
            }
        });
        button_sign_up = findViewById(R.id.button_sign_up);
        button_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
                LogInActivity.this.startActivity(intent);

            }
        });

    }
    @Override
    protected void onRestart() {
        super.onRestart();
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox_autoLogIn) ;
        if (checkBox.isChecked()) {
            // TODO : CheckBox is checked.
            SharedPreferences sharedPreferences =  getSharedPreferences("AutoLogIn", MODE_PRIVATE);
            String Login_User_ID = sharedPreferences.getString("User_ID", "");

            Intent intent = new Intent(LogInActivity.this, RecordActivity.class);
            intent.putExtra("Login_User_ID", Login_User_ID);
            LogInActivity.this.startActivity(intent);
            finish();
            Toast.makeText(getApplicationContext(), "자동 로그인 성공",Toast.LENGTH_SHORT).show();
        } else {
            // TODO : CheckBox is unchecked.
        }

    }

}