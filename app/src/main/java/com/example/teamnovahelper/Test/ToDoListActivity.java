package com.example.teamnovahelper.Test;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.teamnovahelper.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class ToDoListActivity extends AppCompatActivity {
    ListView listview;
    private static final String TODO_JSON = "todo_item_json";
    private static final String TODO = "todo_item";
    // 참고 블로그 : https://codechacha.com/ko/sharedpref_arraylist/
    //setStringArrayPreference 는 ArrayList 를 Json 으로 변환하여 SharedPreferences 로 String 을 저장하는 코드입니다.
    //왜 JSON 을 사용했는가?
    //SharedPreferences 의 value 값을 담는 변수 자료형 중에 ArrayList 는 없습니다. 하지만, 여기서는 Value 값에
    //ArrayList 의 데이터들을 담아야합니다. 그래서 String 형태인 JSON 을 이용하는 것입니다.
    //ArrayList 를 Json 으로 변환하면 String 형태가 되기 때문에 ArrayList 를 SharedPreferences 로 저장할 수 있게 됩니다.

    //ToDoListActivity 는 할 일을 기록할 수 있는 액티비티입니다. 할 일을 수정하거나 삭제할 수 있습니다.

    public void setStringArrayPreference(String key, ArrayList<String> values) {
        SharedPreferences preferences = getSharedPreferences(TODO, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        JSONArray jsonArray = new JSONArray();
        for (int position = 0; position < values.size(); position++) {
            jsonArray.put(values.get(position));
        }
        if (!values.isEmpty()) {
            editor.putString(key, jsonArray.toString());
            Log.d("디버그태그", jsonArray.toString());
        } else {
            editor.putString(key, null);
        }
        editor.commit();
    }
    //getStringArrayPreference 는 SharedPreferences 에서 Json 형식의 String 을 가져와서 다시 ArrayList 로 변환하는 코드입니다.
    public ArrayList<String> getStringArrayPreference(String key) {
        SharedPreferences preferences = getSharedPreferences(TODO, MODE_PRIVATE);
        String json = preferences.getString(key, null);
        ArrayList<String> todo_list = new ArrayList<String>();
        if (json != null) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int length = 0; length < jsonArray.length(); length++) {
                    String ToDo = jsonArray.optString(length);
                    todo_list.add(ToDo);
                }
            } catch (JSONException e) {
                e.printStackTrace(); //에러출력
            }
        }
        return todo_list;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("할일화면", "onCreate() 상태" );
        setContentView(R.layout.activity_to__do__list);
        setTitle("할 일 기록하기");

        //저장한 배경색을 나타내는 부분입니다.
        ConstraintLayout constraint_layout = (ConstraintLayout) findViewById(R.id.todolist_layout);
        SharedPreferences layout_sharedPreferences =  getSharedPreferences("saveColor", MODE_PRIVATE);
        int background_color = layout_sharedPreferences.getInt("Background color", 0);
        constraint_layout.setBackgroundColor(background_color);

        // 빈 데이터 리스트 생성.
        //final ArrayList<String> items = new ArrayList<String>() ;
        // ArrayAdapter 생성. 아이템 View 를 선택(single choice)가능하도록 만들었습니다.
        // 별도의 Adapter 를 구현하지 않고 안드로이드 SDK 에서 제공하는 ArrayAdapter 를 사용하였습니다.
        // 아이템이 TextView 만으로 구성되고(simple_list_item_single_choice) 데이터가 String 배열이기 때문에
        // ArrayAdapter 만으로 그 기능을 제공할 수 있기 때문입니다.

        //ArrayList 객체(items) 생성과 동시에 SharedPreferences 를 사용해 Json 으로 저장된 ArrayList 를 불러오기
        final ArrayList<String> items = getStringArrayPreference(TODO_JSON);
        Log.d("디버그태그", String.valueOf(items));
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, items) ;
        // listview 생성 및 adapter 지정
        listview = findViewById(R.id.listview);
        listview.setAdapter(adapter) ;

        TextView textView_explain = findViewById(R.id.textView_explain3);
        Log.d("어레이리스트 사이즈", String.valueOf(items.size()));
        if(items.size()==0){
            textView_explain.setVisibility(View.VISIBLE);

        }else{
            textView_explain.setVisibility(View.GONE);
        }

        // 추가하기에 대한 이벤트 처리
        Button addButton = (Button)findViewById(R.id.add) ;
        addButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                // 레이아웃 파일 edit_box.xml 을 불러와서 화면에 다이얼로그를 보여줍니다.
                AlertDialog.Builder builder = new AlertDialog.Builder(ToDoListActivity.this);
                View view = LayoutInflater.from(ToDoListActivity.this)
                        .inflate(R.layout.todo_edit_box, null, false);
                builder.setView(view);
                final Button ButtonSubmit = (Button) view.findViewById(R.id.button_dialog_submit);
                final EditText Edit_Todo = (EditText) view.findViewById(R.id.edit_text_dialog_todo);
                ButtonSubmit.setText("할 일 추가하기");
                final AlertDialog dialog = builder.create();
                // 다이얼로그에 있는 삽입 버튼을 클릭하면
                ButtonSubmit.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // 사용자가 입력한 내용을 가져와서
                        String string_todo = Edit_Todo.getText().toString();
                        // ArrayList 에 추가합니다.
                        items.add(string_todo);
                        //ArrayList 를 JSON 으로 변환하여 저장합니다.
                        setStringArrayPreference(TODO_JSON, items);
                        // 어댑터에서 ListView 에 반영하도록 합니다.
                        adapter.notifyDataSetChanged();
                        //다이얼로그를 사라지게 하는 메소드
                        dialog.dismiss();
                        textView_explain.setVisibility(View.GONE);
                    }
                });
                //다이얼로그를 보여주라는 메소드
                dialog.show();
            }
        }) ;
        // 수정하기 버튼에 대한 이벤트 처리
        Button modifyButton = (Button)findViewById(R.id.modify) ;
        modifyButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                int count, checked ;
                count = adapter.getCount() ;
                if (count > 0) {
                    // 현재 선택된 아이템의 position 획득.
                    checked = listview.getCheckedItemPosition();
                    if (checked > -1 && checked < count) {
                        // 레이아웃 파일 todo_edit_box.xml 을 불러와서 화면에 다이얼로그를 보여줍니다.
                        AlertDialog.Builder builder = new AlertDialog.Builder(ToDoListActivity.this);
                        View view = LayoutInflater.from(ToDoListActivity.this)
                                .inflate(R.layout.todo_edit_box, null, false);
                        builder.setView(view);
                        final Button ButtonSubmit = (Button) view.findViewById(R.id.button_dialog_submit);
                        final EditText Edit_Todo = (EditText) view.findViewById(R.id.edit_text_dialog_todo);
                        //해당 줄에 입력되어 있던 데이터를 불러와서 다이얼로그에 보여줍니다.
                        Edit_Todo.setText(items.get(checked));
                        ButtonSubmit.setText("할 일 수정하기");
                        final AlertDialog dialog = builder.create();
                        // 다이얼로그에 있는 할 일 수정하기 버튼을 클릭하면
                        ButtonSubmit.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                // 사용자가 입력한 내용을 가져와서
                                String string_todo = Edit_Todo.getText().toString();
                                // ArrayList 를 수정하고
                                items.set(checked, string_todo);
                                // 하나의 데이터가 수정된 ArrayList 를 JSON 형태로 저장합니다.
                                setStringArrayPreference(TODO_JSON, items);
                                // 어댑터에서 ListView 에 반영하도록 합니다.
                                adapter.notifyDataSetChanged();
                                dialog.dismiss(); //다이얼로그를 사라지게 하는 메소드
                            }
                        });
                        dialog.show(); //다이얼로그를 보여주라는 메소드
                    }
                }
            }
        }) ;
        // delete button 에 대한 이벤트 처리.
        Button deleteButton = (Button)findViewById(R.id.delete) ;
        deleteButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                int count, checked ;
                count = adapter.getCount() ;
                if (count > 0) {
                    // 현재 선택된 아이템의 position 획득.
                    checked = listview.getCheckedItemPosition();
                    if (checked > -1 && checked < count) {
                        // 아이템 삭제
                        items.remove(checked) ;
                        // 하나의 데이터가 삭제된 ArrayList 를 JSON 형태로 저장합니다.
                        setStringArrayPreference(TODO_JSON, items);
                        // listView 선택 초기화
                        listview.clearChoices();
                        // listView 갱신
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }) ;
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.v("할일화면", "onStart() 상태" );
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.v("할일화면", "onStop() 상태" );
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("할일화면", "onDestroy() 상태" );
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.v("할일화면", "onPause() 상태" );
        //ArrayList 를 JSON 으로 변환하여 저장합니다.
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.v("할일화면", "onResume() 상태" );
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v("할일화면", "onRestart() 상태" );
    }
}