package com.example.teamnovahelper.Adapter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamnovahelper.Database.FeedbackDictionary;
import com.example.teamnovahelper.R;

import java.util.ArrayList;
import java.util.Calendar;

public class FeedbackCustomAdapter extends RecyclerView.Adapter<FeedbackCustomAdapter.CustomViewHolder> {
    private ArrayList<FeedbackDictionary> Feedback_ArrayList;
    private Context Context;
    public class CustomViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener { // 리스너 추가
        protected TextView feedback_person;
        protected TextView feedback_date;
        protected TextView feedback_contents;

        Calendar cal = Calendar.getInstance();
        //현재 년도, 월, 일
        int year = cal.get (Calendar.YEAR);
        int month = cal.get (Calendar.MONTH);
        int date = cal.get (Calendar.DATE) ;
        public CustomViewHolder(View view) { //리사이클러뷰에서 보여지는 부분
            super(view);
            this.feedback_person = (TextView) view.findViewById(R.id.name);
            this.feedback_date = (TextView) view.findViewById(R.id.date);
            this.feedback_contents = (TextView) view.findViewById(R.id.feedback_contents_listitem);
            view.setOnCreateContextMenuListener(this); // 리스너 등록
        }
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem Edit = menu.add(Menu.NONE, 1001, 1, "피드백 수정하기");
            MenuItem Delete = menu.add(Menu.NONE, 1002, 2, "피드백 삭제하기");
            Edit.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onEditMenu);
        }

        // 메뉴 클릭시 동작을 설정
        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 1001:
                        //다이얼로그 생성
                        AlertDialog.Builder builder = new AlertDialog.Builder(Context);
                        View view = LayoutInflater.from(Context)
                                .inflate(R.layout.feedback_edit_box, null, false);
                        builder.setView(view);
                        final Button ButtonSubmit = (Button) view.findViewById(R.id.button_dialog_submit);
                        final EditText edit_feedback_person = (EditText) view.findViewById(R.id.edittext_dialog_feedback_person);
                        final Button Button_dialog_feedback_date = (Button) view.findViewById(R.id.Button_dialog_feedback_date);
                        final EditText edit_feedback_contents = (EditText) view.findViewById(R.id.edittext_dialog_feedback_contents);
                        ButtonSubmit.setText("피드백 수정하기");
                        //해당 아이템의 포지션을 찾고, 그 안에있는 데이터를 가져와 다이얼로그에 띄우는 작업
                        edit_feedback_person.setText(Feedback_ArrayList.get(getAdapterPosition()).getFeedback_Person());
                        Button_dialog_feedback_date.setText(Feedback_ArrayList.get(getAdapterPosition()).getFeedback_Date());
                        edit_feedback_contents.setText(Feedback_ArrayList.get(getAdapterPosition()).getFeedback_Contents());
                        final AlertDialog dialog = builder.create();
                        Button_dialog_feedback_date.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                DatePickerDialog datePickerDialog = new DatePickerDialog(Context, new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                        Button_dialog_feedback_date.setText(String.format("%d년 %d월 %d일", year, month + 1, dayOfMonth));
                                    }
                                }, year, month, date);

                                datePickerDialog.setMessage("피드백 받은 날짜 선택하기");
                                datePickerDialog.show();
                            }
                        });
                        ButtonSubmit.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                //사용자가 수정한 데이터를 다시 어레이 리스트에 반영하고, 어댑터가 다시 그걸 반영해서 리사이클러뷰에 반영하는 과정
                                String string_feedback_person = edit_feedback_person.getText().toString();
                                String string_feedback_date = Button_dialog_feedback_date.getText().toString();
                                String string_feedback_contents = edit_feedback_contents.getText().toString();
                                FeedbackDictionary dict = new FeedbackDictionary(string_feedback_person, string_feedback_date, string_feedback_contents);
                                Feedback_ArrayList.set(getAdapterPosition(), dict);
                                notifyItemChanged(getAdapterPosition());
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        break;
                    case 1002:
                        Feedback_ArrayList.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                        notifyItemRangeChanged(getAdapterPosition(), Feedback_ArrayList.size());

                        break;
                }
                return true;
            }
        };
    }

//CustomAdapter 의 생성자
    public FeedbackCustomAdapter(Context context, ArrayList<FeedbackDictionary> list) {
        Feedback_ArrayList = list;
        Context = context;
    }
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    //뷰홀더 객체를 만들어 주는 곳
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.feedback_list, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {
        //생성된 뷰홀더에 데이터를 묶어주는(바인딩) 메소드
        //스크롤을 해서 데이터 바인딩이 필요할 때마다 호출되는 함수다.
        //데이터를 추가할 때는 onCreateViewHolder 와 onBindViewHolder 둘다 호출되고,
        //스크롤을 해서 뷰홀더를 재사용할때는 onBindViewHolder 만 호출된다.
//        viewholder.feedback_person.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
//        viewholder.feedback_date.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
//        viewholder.feedback_contents.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
//
//        viewholder.feedback_person.setGravity(Gravity.CENTER);
//        viewholder.feedback_date.setGravity(Gravity.CENTER);
//        viewholder.feedback_contents.setGravity(Gravity.CENTER);

        viewholder.feedback_person.setText(Feedback_ArrayList.get(position).getFeedback_Person());
        viewholder.feedback_date.setText(Feedback_ArrayList.get(position).getFeedback_Date());
        viewholder.feedback_contents.setText(Feedback_ArrayList.get(position).getFeedback_Contents());
    }
    @Override
    public int getItemCount() {
        return (null != Feedback_ArrayList ? Feedback_ArrayList.size() : 0);
    }
//데이터의 길이를 측정하는 메소드
}