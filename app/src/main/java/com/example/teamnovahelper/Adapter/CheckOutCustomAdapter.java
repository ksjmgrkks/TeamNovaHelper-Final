package com.example.teamnovahelper.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.teamnovahelper.Database.CheckOutDictionary;
import com.example.teamnovahelper.R;

import java.util.ArrayList;

public class CheckOutCustomAdapter extends RecyclerView.Adapter<CheckOutCustomAdapter.CheckOutCustomViewHolder> {

    private ArrayList<CheckOutDictionary> List;
    private Context Context;

    public class CheckOutCustomViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener { // 리스너 추가
        protected ImageView imageView;
        protected ImageView imageViewSort;
        protected TextView name;
        protected TextView date;



        public CheckOutCustomViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.imageView_marker);
            this.imageViewSort = (ImageView) view.findViewById(R.id.imageView_sort);
            this.name = (TextView) view.findViewById(R.id.name);
            this.date = (TextView) view.findViewById(R.id.date);


            view.setOnCreateContextMenuListener(this); // 리스너 등록
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {  // 메뉴 추가


//            MenuItem Edit = menu.add(Menu.NONE, 1001, 1, "편집");
            MenuItem Delete = menu.add(Menu.NONE, 1001, 1, "삭제");
//            Edit.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onEditMenu);

        }

        // 메뉴 클릭시 동작을 설정
        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {


                switch (item.getItemId()) {
                    case 1001:
                        List.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                        notifyItemRangeChanged(getAdapterPosition(), List.size());
                        break;

                }
                return true;
            }
        };


    }


    public CheckOutCustomAdapter(Context context, ArrayList<CheckOutDictionary> list) {
        List = list;
        Context = context;
    }


    @Override
    public CheckOutCustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.checkout_list, viewGroup, false);

        CheckOutCustomViewHolder viewHolder = new CheckOutCustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CheckOutCustomViewHolder viewholder, int position) {

        Glide.with(Context).load(List.get(position).getSort()).into(viewholder.imageViewSort);
        Bitmap bitmap = StringToBitMap(List.get(position).getImage());
        Glide.with(Context).load(bitmap).into(viewholder.imageView);
        Log.i("뷰홀더 이미지에 뭐가 들어있을까?", List.get(position).getImage());
        viewholder.name.setText(List.get(position).getName());
        viewholder.date.setText(List.get(position).getDate());

    }

    @Override
    public int getItemCount() {
        return (null != List ? List.size() : 0);
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}
