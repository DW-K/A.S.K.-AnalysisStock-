package com.gachon.ask;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gachon.ask.community.CategoryActivity;
import com.gachon.ask.community.PostActivity;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<ViewHolder> {
    CategoryActivity categoryActivity;
    List<WriteInfo> writeInfoList;


    public CustomAdapter(CategoryActivity categoryActivity, List<WriteInfo> writeInfoList) {
        this.categoryActivity = categoryActivity;
        this.writeInfoList = writeInfoList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate layout
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.model_layout, viewGroup,false);

        ViewHolder viewHolder = new ViewHolder(itemView);


        // item clicked
        viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Intent intent = new Intent(view.getContext(), PostActivity.class);
                intent.putExtra("nickname",writeInfoList.get(position).getNickname());
                intent.putExtra("contents",writeInfoList.get(position).getContents());
                intent.putExtra("publisher",writeInfoList.get(position).getPublisher());
                intent.putExtra("selectedCategory",writeInfoList.get(position).getCategory());
                intent.putExtra("created_at",getTime(writeInfoList.get(position).getCreatedAt()));
                intent.putExtra("posts_id", writeInfoList.get(position).getPosts_id());

                categoryActivity.startActivity(intent);

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }

        });


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        //bind views
        viewHolder.vContents.setText(writeInfoList.get(i).getContents());
        viewHolder.vNickname.setText(writeInfoList.get(i).getNickname());
        viewHolder.vUploadTime.setText(getTime(writeInfoList.get(i).getCreatedAt()));

    }

    @Override
    public int getItemCount() {
        return writeInfoList.size();
    }


    // getExtra cannot call the type timestamp
    // cast the type timestamp -> String
    static String getTime(Timestamp time) {
        Date date_createdAt = time.toDate();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy년 MM월 HH시 mm분 ss초");
        String txt_createdAt = formatter.format(date_createdAt);
        return txt_createdAt;
    }


}
