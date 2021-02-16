package com.example.teamnovahelper.MainActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamnovahelper.Adapter.CheckOutCustomAdapter;
import com.example.teamnovahelper.Database.CheckOutDictionary;
import com.example.teamnovahelper.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CheckOutActivity extends AppCompatActivity {

    private ArrayList<CheckOutDictionary> myArrayList;
    private CheckOutCustomAdapter myAdapter;
    private final int REQUEST_IMAGE_CAPTURE = 200;
    private static final String CheckOut_JSON = "CheckOut_item_json";
    private static final String CheckOut = "CheckOut_item";
    private TextView textView_explain;
    String Image;
    String[] User;
    int sort;
    long now = System.currentTimeMillis();
    //todo: 가져온거라도 썼으면 내책임이다.
    Date date = new Date(now);
    SimpleDateFormat formatNow = new SimpleDateFormat("yyyy/MM/dd HH시 mm분");
    String formatDate = formatNow.format(date);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        setTitle("출근/퇴근 기록");

        //저장한 배경색을 나타내는 부분입니다.
        ConstraintLayout constraint_layout = (ConstraintLayout) findViewById(R.id.CheckOut_ConstraintLayout);
        SharedPreferences layout_sharedPreferences =  getSharedPreferences("saveColor", MODE_PRIVATE);
        int background_color = layout_sharedPreferences.getInt("Background color", 0);
        constraint_layout.setBackgroundColor(background_color);

        String Login_User_ID = getIntent().getStringExtra("Login_User_ID");
        SharedPreferences sharedPreferences =  getSharedPreferences("saveID", MODE_PRIVATE);
        String User_information = sharedPreferences.getString(Login_User_ID, "");
        User = User_information.split(",");

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_todo);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);


        myArrayList = new ArrayList<>();

        myAdapter = new CheckOutCustomAdapter( this, myArrayList);
        mRecyclerView.setAdapter(myAdapter);

        //SharedPreferences 에서 저장한 json 불러오기 -> 어레이 리스트 -> 리사이클러뷰에 표현
        SharedPreferences preferences = getSharedPreferences(CheckOut, MODE_PRIVATE);
        String json = preferences.getString(CheckOut_JSON, null);
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < jsonArray.length() ; i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String image = jsonObject.getString("CheckOut_Image");
                String name = jsonObject.getString("CheckOut_Name");
                String date = jsonObject.getString("CheckOut_Date");
                String sort = jsonObject.getString("CheckOut_Sort");

                CheckOutDictionary dict = new CheckOutDictionary(image, name, date, sort );
                myArrayList.add(dict);
                myAdapter.notifyItemInserted(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        textView_explain = findViewById(R.id.textView_explain);
        Log.d("어레이리스트 사이즈", String.valueOf(myArrayList.size()));
        if(myArrayList.size()==0){
            textView_explain.setVisibility(View.VISIBLE);
        }else{
            textView_explain.setVisibility(View.GONE);
        }

        FloatingActionButton buttonInsert = (FloatingActionButton)findViewById(R.id.Button_main_insert);
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            // 화면 아래쪽에 있는 피드백 추가하기 버튼을 클릭하면
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CheckOutActivity.this);
                builder.setTitle("출근/퇴근 선택");
                builder.setItems(R.array.Sort_Pick, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int position)
                    {

                        String[] items = getResources().getStringArray(R.array.Sort_Pick);
                        switch(items[position]){
                            case "출근 기록하기" :
                                textView_explain.setVisibility(View.GONE);
                                sort = 0;
                                Toast.makeText(getApplicationContext(), "출근사진을 찍어주세요",Toast.LENGTH_SHORT).show();
                                sendTakePhotoIntent();
                                break;
                            case "퇴근 기록하기":
                                textView_explain.setVisibility(View.GONE);
                                sort = 1;
                                Toast.makeText(getApplicationContext(), "퇴근 사진을 찍어주세요",Toast.LENGTH_SHORT).show();
                                sendTakePhotoIntent();
                                break;
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }
    private void sendTakePhotoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Image = BitMapToString(imageBitmap);
            CheckOutDictionary dictionary;//첫번째 줄에 삽입됨
            if(sort == 0){
                dictionary = new CheckOutDictionary(Image, User[1], formatDate, "https://ifh.cc/g/n4DMvX.png");
                //mArrayList.add(dict); //마지막 줄에 삽입됨
            }else{
                dictionary = new CheckOutDictionary(Image, User[1], formatDate, "https://ifh.cc/g/9abMg7.png");
                //mArrayList.add(dict); //마지막 줄에 삽입됨
            }
            myArrayList.add(0, dictionary); //첫번째 줄에 삽입됨
            myAdapter.notifyItemInserted(0);
            myAdapter.notifyDataSetChanged();

        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        //어레이리스트 -> JSON -> SharedPreferences 에 저장
        SharedPreferences preferences = getSharedPreferences(CheckOut, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i <myArrayList.size() ; i++){
                JSONObject Object = new JSONObject();//배열 내에 들어갈 jsonObject
                Object.put("CheckOut_Image",myArrayList.get(i).getImage());
                Object.put("CheckOut_Name",myArrayList.get(i).getName());
                Object.put("CheckOut_Date",myArrayList.get(i).getDate());
                Object.put("CheckOut_Sort",myArrayList.get(i).getSort());
                jsonArray.put(Object);
            }
            editor.putString(CheckOut_JSON, jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editor.commit();
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

}