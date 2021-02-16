package com.example.teamnovahelper.Test;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamnovahelper.Adapter.TestAdapter;
import com.example.teamnovahelper.Database.TestDictionary;
import com.example.teamnovahelper.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CheckOutTestActivity extends AppCompatActivity {
    //원래 CheckOutActivity 가 메인이었는데,
    //Test 버전을 따로 만들어 쓰다가 Test 버전이 더 빨리 구현할 수 있어서 Test 버전을 메인으로 쓰게 되었습니다.
    //CheckOutActivity 는 팀노바에 출퇴근 할 때 이름과 현재 시간을 기록하는 액티비티입니다.
    //출근 or 퇴근을 선택하면, 로그인 한 이름, 출근 or 퇴근, 현재 시간이 리사이클러뷰에 기록됩니다.
//todo : 제발 변수명 통일
    private ArrayList<TestDictionary> Test_ArrayList;
    private TestAdapter Test_Adapter;
    private static final String CheckOut_JSON = "CheckOut_item_json";
    private static final String CheckOut = "CheckOut_item";
    private TextView textView_explain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out_test);
        setTitle("출/퇴근 기록");

        //저장한 배경색을 나타내는 부분입니다.
        ConstraintLayout constraint_layout = (ConstraintLayout) findViewById(R.id.CheckOut_ConstraintLayout);
        SharedPreferences layout_sharedPreferences =  getSharedPreferences("saveColor", MODE_PRIVATE);
        int background_color = layout_sharedPreferences.getInt("Background color", 0);
        constraint_layout.setBackgroundColor(background_color);

        String Login_User_ID = getIntent().getStringExtra("Login_User_ID");

        SharedPreferences sharedPreferences =  getSharedPreferences("saveID", MODE_PRIVATE);
        String User_information = sharedPreferences.getString(Login_User_ID, "");
        String[] User = User_information.split(",");

        //아이템뷰가 나열되는 형태를 관리하기위한 레이아웃매니저 객체 생성
        RecyclerView Test_RecyclerView = (RecyclerView) findViewById(R.id.recyclerview_todo);
        LinearLayoutManager TestLinearLayoutManager = new LinearLayoutManager(this);
        Test_RecyclerView.setLayoutManager(TestLinearLayoutManager);
        //어레이리스트와 어탭터 연결
        //todo: Test 라는 의미가 불명확하고, check_out_array_list 이런식으로 알아보게 써야함.
        Test_ArrayList = new ArrayList<>();

        Test_Adapter = new TestAdapter( this, Test_ArrayList);
        Test_RecyclerView.setAdapter(Test_Adapter);
        //리사이클러뷰 아이템간의 구분선 설정
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(Test_RecyclerView.getContext(),
                TestLinearLayoutManager.getOrientation());
        Test_RecyclerView.addItemDecoration(dividerItemDecoration);
        Button buttonInsert = (Button)findViewById(R.id.Button_main_insert);

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
                String person = jsonObject.getString("CheckOut_Person");
                String sort = jsonObject.getString("CheckOut_Sort");
                String date = jsonObject.getString("CheckOut_Date");

                TestDictionary dict = new TestDictionary(person, sort, date );
                Test_ArrayList.add(dict);
                Test_Adapter.notifyItemInserted(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        textView_explain = findViewById(R.id.textView_explain);
        Log.d("어레이리스트 사이즈", String.valueOf(Test_ArrayList.size()));
        if(Test_ArrayList.size()==0){
            textView_explain.setVisibility(View.VISIBLE);

        }else{
            textView_explain.setVisibility(View.GONE);
        }

        buttonInsert.setOnClickListener(new View.OnClickListener() {
            // 화면 아래쪽에 있는 피드백 추가하기 버튼을 클릭하면
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(CheckOutTestActivity.this);
                builder.setTitle("출근/퇴근 선택");

                builder.setItems(R.array.Sort_Pick, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int position)
                    {
                        long now = System.currentTimeMillis();
                        //todo: 가져온거라도 썼으면 내책임이다.
                        Date date = new Date(now);
                        SimpleDateFormat formatNow = new SimpleDateFormat("yyyy/MM/dd\nHH시 mm분");
                        String formatDate = formatNow.format(date);

                        String[] items = getResources().getStringArray(R.array.Sort_Pick);
                        switch(items[position]){

                            case "출근 기록하기" :
                                textView_explain.setVisibility(View.GONE);
                                TestDictionary dict = new TestDictionary(User[1],"출근", formatDate );
                                Test_ArrayList.add(0, dict); //첫번째 줄에 삽입됨
                                Log.d("디버그 어레이리스트 입력", String.valueOf(Test_ArrayList));
                                Test_Adapter.notifyItemInserted(0);
                                Test_Adapter.notifyDataSetChanged();
                                break;

                            case "퇴근 기록하기":
                                textView_explain.setVisibility(View.GONE);
                                dict = new TestDictionary(User[1],"퇴근", formatDate );
                                Test_ArrayList.add(0, dict); //첫번째 줄에 삽입됨
                                Log.d("디버그 어레이리스트 입력", String.valueOf(Test_ArrayList));
                                Test_Adapter.notifyItemInserted(0);
                                Test_Adapter.notifyDataSetChanged();
                                break;
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        //어레이리스트 -> JSON -> SharedPreferences 에 저장
        SharedPreferences preferences = getSharedPreferences(CheckOut, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i <Test_ArrayList.size() ; i++){
                JSONObject Object = new JSONObject();//배열 내에 들어갈 jsonObject
                Object.put("CheckOut_Person",Test_ArrayList.get(i).getName());
                Object.put("CheckOut_Sort",Test_ArrayList.get(i).getSort());
                Object.put("CheckOut_Date",Test_ArrayList.get(i).getDate());
                jsonArray.put(Object);
            }
            editor.putString(CheckOut_JSON, jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editor.commit();
    }

}