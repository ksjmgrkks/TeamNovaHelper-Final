package com.example.teamnovahelper.Adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamnovahelper.Database.TestDictionary;
import com.example.teamnovahelper.R;

import java.util.ArrayList;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.TestViewHolder>{
    private ArrayList<TestDictionary> Test_ArrayList;
    private android.content.Context Context;
    public class TestViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener { // 리스너 추가
        protected TextView checkout_person;
        protected TextView checkout_sort;
        protected TextView checkout_date;
        public TestViewHolder(View view) { //리사이클러뷰에서 보여지는 부분
            super(view);
            this.checkout_person = (TextView) view.findViewById(R.id.name);
            this.checkout_sort = (TextView) view.findViewById(R.id.checkout_sort_listitem);
            this.checkout_date = (TextView) view.findViewById(R.id.checkout_date_listitem);
            view.setOnCreateContextMenuListener(this); // 리스너 등록
        }
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem Delete = menu.add(Menu.NONE, 1002, 2, "출퇴근 기록 삭제하기");
            Delete.setOnMenuItemClickListener(onEditMenu);
        }

        // 메뉴 클릭시 동작을 설정
        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 1002:
                        Test_ArrayList.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                        notifyItemRangeChanged(getAdapterPosition(), Test_ArrayList.size());

                        break;
                }
                return true;
            }
        };
    }

    //CustomAdapter 의 생성자
    public TestAdapter(Context context, ArrayList<TestDictionary> list) {
        Test_ArrayList = list;
        Context = context;
    }
    @Override
    public TestAdapter.TestViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        //뷰홀더 객체를 만들어 주는 곳
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.test_list, viewGroup, false);
        TestAdapter.TestViewHolder viewHolder = new TestAdapter.TestViewHolder(view);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull TestAdapter.TestViewHolder viewholder, int position) {
        //생성된 뷰홀더에 데이터를 묶어주는(바인딩) 메소드
        //스크롤을 해서 데이터 바인딩이 필요할 때마다 호출되는 함수다.
        //데이터를 추가할 때는 onCreateViewHolder 와 onBindViewHolder 둘다 호출되고,
        //스크롤을 해서 뷰홀더를 재사용할때는 onBindViewHolder 만 호출된다.
        viewholder.checkout_person.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        viewholder.checkout_sort.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        viewholder.checkout_date.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        viewholder.checkout_person.setGravity(Gravity.CENTER);
        viewholder.checkout_sort.setGravity(Gravity.CENTER);
        viewholder.checkout_date.setGravity(Gravity.CENTER);

        viewholder.checkout_person.setText(Test_ArrayList.get(position).getName());
        viewholder.checkout_sort.setText(Test_ArrayList.get(position).getSort());
        viewholder.checkout_date.setText(Test_ArrayList.get(position).getDate());
    }
    @Override
    public int getItemCount() {
        return (null != Test_ArrayList ? Test_ArrayList.size() : 0);
    }
//데이터의 길이를 측정하는 메소드
}
