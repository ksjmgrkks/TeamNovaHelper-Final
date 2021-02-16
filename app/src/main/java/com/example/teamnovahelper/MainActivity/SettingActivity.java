package com.example.teamnovahelper.MainActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.teamnovahelper.R;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import petrov.kristiyan.colorpicker.ColorPicker;

public class SettingActivity extends AppCompatActivity {
    //SettingActivity 는 로그인 한 회원정보를 나타내고,
    //프로필사진 변경, 로그 아웃, 회원 탈퇴, 어플 배경 색상 바꾸기와 같은 기능을 담은 액티비티입니다.

    private Button button_logout;
    private Button button_leave;
    private Button button_change_color;
    private ConstraintLayout constraint_layout;
    private TextView textview_setting_id;
    private TextView textview_setting_name;
    private CircleImageView imageView_profile;
    String Login_User_ID;

    private static final int MY_PERMISSION_CAMERA = 1111;
    private final int GET_GALLERY_IMAGE = 2222;
    private static final int REQUEST_IMAGE_CAPTURE = 3333;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitle("설정");

        Login_User_ID = getIntent().getStringExtra("Login_User_ID");

        //기본프로필 이미지를 비트맵으로 표현 -> String 전환 -> defValue에 넣으면
        //저장된 프로필 이미지가 없을 때 basic_profile 이 기본 프로필 이미지로 설정된다.
        Bitmap basic_image = BitmapFactory.decodeResource(getResources(), R.drawable.basic_profile);
        String basic_profile = BitMapToString(basic_image);

        SharedPreferences profile_SharedPreferences =  getSharedPreferences("saveProfile", MODE_PRIVATE);
        String profile = profile_SharedPreferences.getString(Login_User_ID, basic_profile);
        Bitmap imageBitmap = StringToBitMap(profile);
        ((ImageView) findViewById(R.id.imageView_profile)).setImageBitmap(imageBitmap);

        SharedPreferences mSharedPreferences =  getSharedPreferences("saveID", MODE_PRIVATE);
        String User_information = mSharedPreferences.getString(Login_User_ID, "");
        String[] User = User_information.split(",");

        textview_setting_id = findViewById(R.id.textview_setting_id);
        textview_setting_name = findViewById(R.id.textview_setting_name);


        textview_setting_id.setText(Login_User_ID);
        textview_setting_name.setText(User[1]);

        button_change_color = (Button) findViewById(R.id.button_change_color);
        button_leave = (Button) findViewById(R.id.button_leave);
        button_logout = (Button) findViewById(R.id.button_logout);
        constraint_layout = (ConstraintLayout) findViewById(R.id.constraint_layout);
        imageView_profile = (CircleImageView) findViewById(R.id.imageView_profile);


        SharedPreferences sharedPreferences = getSharedPreferences("saveColor", MODE_PRIVATE);
        int background_color = sharedPreferences.getInt("Background color", 0);
        constraint_layout.setBackgroundColor(background_color);

        checkPermission();

        imageView_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);

                builder.setTitle("프로필 변경");

                builder.setItems(R.array.Image_Pick, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int position)
                    {
                        String[] items = getResources().getStringArray(R.array.Image_Pick);
                        switch(items[position]){
                          case "카메라에서 가져오기" :
                              sendTakePhotoIntent();
                            break;

                          case "갤러리에서 가져오기":
                              Intent intent = new Intent();
                              intent.setType("image/*");
                              intent.setAction(Intent.ACTION_GET_CONTENT);
                              startActivityForResult(intent,GET_GALLERY_IMAGE);
                            break;
                        }
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor;
                SharedPreferences sharedPreferences = getSharedPreferences("saveAutoLogIn", MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putBoolean("Auto_Log_in", false);
                editor.apply();

                Intent intent = new Intent(SettingActivity.this, LogInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                SettingActivity.this.startActivity(intent);

                Toast.makeText(getApplicationContext(), "로그아웃 완료", Toast.LENGTH_SHORT).show();
            }
        });

        button_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("정말로 아이디를 삭제하시겠습니까?");

                builder.setItems(R.array.Yes_Or_No, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int position)
                    {
                        String[] items = getResources().getStringArray(R.array.Yes_Or_No);
                        switch(items[position]){
                            case "예" :
                                SharedPreferences sharedPreferences =  getSharedPreferences("saveID", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.remove(Login_User_ID);
                                editor.apply();

                                Intent intent = new Intent(SettingActivity.this, LogInActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                SettingActivity.this.startActivity(intent);
                                Toast.makeText(getApplicationContext(), "회원탈퇴 완료", Toast.LENGTH_SHORT).show();
                                break;
                            case "아니요":
                                break;
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        button_change_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openColorPicker();
            }
        });
    }

    public void openColorPicker() {
        final ColorPicker colorPicker = new ColorPicker(this);  // ColorPicker 객체 생성
        ArrayList<String> colors = new ArrayList<>();  // Color 넣어줄 list

        colors.add("#FFFFFF");
        colors.add("#ffab91");
        colors.add("#F48FB1");
        colors.add("#ce93d8");
        colors.add("#b39ddb");
        colors.add("#9fa8da");
        colors.add("#90caf9");
        colors.add("#81d4fa");
        colors.add("#80deea");
        colors.add("#80cbc4");
        colors.add("#c5e1a5");
        colors.add("#e6ee9c");
        colors.add("#fff59d");
        colors.add("#ffe082");
        colors.add("#ffcc80");

        colorPicker.setColors(colors)  // 만들어둔 list 적용
                .setColumns(5)  // 5열로 설정
                .setRoundColorButton(true)  // 원형 버튼으로 설정
                .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {// OK 버튼 클릭 시 이벤트
                        SharedPreferences.Editor editor;
                        SharedPreferences sharedPreferences = getSharedPreferences("saveColor", MODE_PRIVATE);
                        editor = sharedPreferences.edit();
                        editor.putInt("Background color", color);
                        editor.apply();
                        constraint_layout.setBackgroundColor(color);

                    }

                    @Override
                    public void onCancel() {
                        // Cancel 버튼 클릭 시 이벤트
                    }
                }).show();  // dialog 생성
    }

    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 처음 호출시엔 if()안의 부분은 false로 리턴 됨 -> else{..}의 요청으로 넘어감
            if ((ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) ||
                    (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))) {
                new AlertDialog.Builder(this)
                        .setTitle("알림")
                        .setMessage("저장소 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, MY_PERMISSION_CAMERA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_CAMERA:
                for (int i = 0; i < grantResults.length; i++) {
                    // grantResults[] : 허용된 권한은 0, 거부한 권한은 -1
                    if (grantResults[i] < 0) {
                        Toast.makeText(SettingActivity.this, "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                // 허용했다면 이 부분에서..

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//갤러리에 있는 사진데이터를 다이얼로그의 이미지뷰에 적용하는 부분입니다.
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GET_GALLERY_IMAGE)
        {
            if(resultCode == RESULT_OK)
            {
                try{
                    InputStream in = getContentResolver().openInputStream(data.getData());

                    Bitmap imageBitmap = BitmapFactory.decodeStream(in);
                    in.close();

                    ((ImageView) findViewById(R.id.imageView_profile)).setImageBitmap(imageBitmap);

                    //비트맵->스트링 후 쉐어드에 저장하기
                    String profile = BitMapToString(imageBitmap);
                    SharedPreferences.Editor editor;
                    SharedPreferences sharedPreferences = getSharedPreferences("saveProfile", MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                    editor.putString(Login_User_ID, profile);
                    editor.apply();

                }catch(Exception e){}
            }
            else if(resultCode == RESULT_CANCELED){}
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ((ImageView) findViewById(R.id.imageView_profile)).setImageBitmap(imageBitmap);

            //비트맵->스트링 후 쉐어드에 저장하기
            String profile = BitMapToString(imageBitmap);
            SharedPreferences.Editor editor;
            SharedPreferences sharedPreferences = getSharedPreferences("saveProfile", MODE_PRIVATE);
            editor = sharedPreferences.edit();
            editor.putString(Login_User_ID, profile);
            editor.apply();

        }
    }

    private void sendTakePhotoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    public String BitMapToString(Bitmap bitmap){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);

        return temp;

    }
    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

}
