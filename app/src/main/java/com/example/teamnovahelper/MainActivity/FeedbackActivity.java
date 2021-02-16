package com.example.teamnovahelper.MainActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamnovahelper.Adapter.FeedbackCustomAdapter;
import com.example.teamnovahelper.Database.FeedbackDictionary;
import com.example.teamnovahelper.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class FeedbackActivity extends AppCompatActivity {
    //FeedbackActivity 는 팀노바에서 받은 모든 피드백들을 기록하는 액티비티입니다.
    //리사이클러뷰 하나의 아이템에 피드백 받은 사람, 날짜, 피드백 내용 이렇게 3가지의 정보를 입력할 수 있습니다.
    private ArrayList<FeedbackDictionary> Feedback_ArrayList;
    private FeedbackCustomAdapter Feedback_Adapter;
    private int count = -1;
    private static final String Feedback_JSON = "Feedback_item_json";
    private static final String Feedback = "Feedback_item";

    Calendar cal = Calendar.getInstance();
    //현재 년도, 월, 일
    int year = cal.get (Calendar.YEAR);
    int month = cal.get (Calendar.MONTH);
    int date = cal.get (Calendar.DATE) ;
    //현재 (시,분,초)
//    int hour = cal.get ( cal.HOUR_OF_DAY ) ;
//    int min = cal.get ( cal.MINUTE );
//    int sec = cal.get ( cal.SECOND );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        setTitle("피드백 기록");

        //저장한 배경색을 나타내는 부분입니다.
        ConstraintLayout constraint_layout = (ConstraintLayout) findViewById(R.id.Feedback_ConstraintLayout);
        SharedPreferences sharedPreferences =  getSharedPreferences("saveColor", MODE_PRIVATE);
        int background_color = sharedPreferences.getInt("Background color", 0);
        constraint_layout.setBackgroundColor(background_color);

        //아이템뷰가 나열되는 형태를 관리하기위한 레이아웃매니저 객체 생성
        RecyclerView Feedback_RecyclerView = (RecyclerView) findViewById(R.id.recyclerview_todo);
        LinearLayoutManager FeedbackLinearLayoutManager = new LinearLayoutManager(this);
        Feedback_RecyclerView.setLayoutManager(FeedbackLinearLayoutManager);
        //어레이리스트와 어탭터 연결
        Feedback_ArrayList = new ArrayList<>();

        Feedback_Adapter = new FeedbackCustomAdapter( this, Feedback_ArrayList);
        Feedback_RecyclerView.setAdapter(Feedback_Adapter);
        //리사이클러뷰 아이템간의 구분선 설정
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(Feedback_RecyclerView.getContext(),
//                FeedbackLinearLayoutManager.getOrientation());
//        Feedback_RecyclerView.addItemDecoration(dividerItemDecoration);

        FloatingActionButton FloatingActionButtonInsert = (FloatingActionButton)findViewById(R.id.Button_main_insert);

        //SharedPreferences 에서 저장한 json 불러오기 -> 어레이 리스트 -> 리사이클러뷰에 표현
        SharedPreferences preferences = getSharedPreferences(Feedback, MODE_PRIVATE);
        String json = preferences.getString(Feedback_JSON, null);
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("디버그첫시작jsonArray", String.valueOf(jsonArray));
        for (int i = 0; i < jsonArray.length() ; i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Log.d("디버그첫시작jsonObject", String.valueOf(jsonObject));
                String person = jsonObject.getString("Feedback_Person");
                Log.d("디버그첫시작person", person);
                String date = jsonObject.getString("Feedback_Date");
                Log.d("디버그첫시작date",date);
                String contents = jsonObject.getString("Feedback_Contents");
                Log.d("디버그첫시작contents", contents);

                FeedbackDictionary dict = new FeedbackDictionary(person, date, contents );
                Feedback_ArrayList.add(dict); //첫번째 줄에 삽입됨

                Feedback_Adapter.notifyItemInserted(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        TextView textView_explain = findViewById(R.id.textView_explain);
        Log.d("어레이리스트 사이즈", String.valueOf(Feedback_ArrayList.size()));
        if(Feedback_ArrayList.size()==0){
            textView_explain.setVisibility(View.VISIBLE);

        }else{
            textView_explain.setVisibility(View.GONE);
        }

        FloatingActionButtonInsert.setOnClickListener(new View.OnClickListener() {
            // 화면 아래쪽에 있는 피드백 추가하기 버튼을 클릭하면
            @Override
            public void onClick(View v) {
                // 레이아웃 파일 feedback_edit_box.xml 을 불러와서 화면에 다이얼로그를 보여줍니다.
                AlertDialog.Builder builder = new AlertDialog.Builder(FeedbackActivity.this);
                View view = LayoutInflater.from(FeedbackActivity.this)
                        .inflate(R.layout.feedback_edit_box, null, false);
                builder.setView(view);
                final Button ButtonSubmit = (Button) view.findViewById(R.id.button_dialog_submit);
                final EditText edit_feedback_person = (EditText) view.findViewById(R.id.edittext_dialog_feedback_person);
                final Button Button_dialog_feedback_date = (Button) view.findViewById(R.id.Button_dialog_feedback_date);
                final EditText edit_feedback_contents = (EditText) view.findViewById(R.id.edittext_dialog_feedback_contents);
                ButtonSubmit.setText("피드백 추가하기");
                final AlertDialog dialog = builder.create();

                Button_dialog_feedback_date.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(FeedbackActivity.this/*, R.style.MySpinnerDatePickerStyle*/,new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                Button_dialog_feedback_date.setText(String.format("%d년 %d월 %d일", year,month+1,dayOfMonth));
                            }
                        },year, month, date);
                        datePickerDialog.setMessage("피드백 받은 날짜 선택하기");
                        datePickerDialog.show();
                    }
                });
                // 다이얼로그에 있는 삽입 버튼을 클릭하면
                ButtonSubmit.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // 사용자가 입력한 내용을 가져와서
                        String string_feedback_person = edit_feedback_person.getText().toString();
                        String string_feedback_date = Button_dialog_feedback_date.getText().toString();
                        String string_feedback_contents = edit_feedback_contents.getText().toString();
                        // ArrayList 에 추가하고
                        FeedbackDictionary dict = new FeedbackDictionary(string_feedback_person, string_feedback_date, string_feedback_contents );
                        Feedback_ArrayList.add(0, dict); //첫번째 줄에 삽입됨
                        Log.d("디버그 어레이리스트 입력", String.valueOf(Feedback_ArrayList));
                        //Feedback_ArrayList.add(dict); //마지막 줄에 삽입됨
                        // 어댑터에서 RecyclerView 에 반영하도록 합니다.
                        Feedback_Adapter.notifyItemInserted(0);
                        //mAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                        textView_explain.setVisibility(View.GONE);
                    }
                });
                dialog.show();
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.v("피드백화면", "onStart() 상태" );
    }
    @Override
    protected void onStop() {
        super.onStop();
        //어레이리스트 -> JSON -> SharedPreferences 에 저장
        SharedPreferences preferences = getSharedPreferences(Feedback, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i <Feedback_ArrayList.size() ; i++){
                JSONObject Object = new JSONObject();//배열 내에 들어갈 json
                Object.put("Feedback_Person",Feedback_ArrayList.get(i).getFeedback_Person());
                Log.d("디버그 사람", Feedback_ArrayList.get(i).getFeedback_Person());
                Object.put("Feedback_Date",Feedback_ArrayList.get(i).getFeedback_Date());
                Log.d("디버그 날짜", Feedback_ArrayList.get(i).getFeedback_Date());
                Object.put("Feedback_Contents",Feedback_ArrayList.get(i).getFeedback_Contents());
                Log.d("디버그 내용", Feedback_ArrayList.get(i).getFeedback_Contents());
                jsonArray.put(Object);
            }
            editor.putString(Feedback_JSON, jsonArray.toString());
            Log.d("디버그 피드백 리사이클러뷰 저장", jsonArray.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        editor.commit();
    }

}