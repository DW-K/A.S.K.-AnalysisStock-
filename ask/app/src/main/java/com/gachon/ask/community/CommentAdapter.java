package com.gachon.ask.community;

import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.gachon.ask.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    ArrayList<CommentInfo> items = new ArrayList<CommentInfo>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.item_comment, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        CommentInfo item = items.get(position);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        String post_id;
        TextView vContents;
        TextView vNickname;
        TextView vUploadTime;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            vNickname = itemView.findViewById(R.id.nickname); // 닉네임
            vContents = (TextView) itemView.findViewById(R.id.comment); //댓글 내용
            vUploadTime = (TextView) itemView.findViewById(R.id.time);// 댓글 단 시간

        }

        public void setItem(CommentInfo item) {
            post_id = item.getPost_id();
//            profileImg = item.getProfile_image();
            vNickname.setText(item.getNickname());// 닉네임
            vContents.setText(item.getComment());//댓글 내용
            vUploadTime.setText(item.getTime());// 댓글 단 시간



        }

    }

    public void addItem(CommentInfo item) {
        items.add(item);
    }

    public void setItems(ArrayList<CommentInfo> items) {
        this.items = items;
    }

    public CommentInfo getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, CommentInfo item) {
        items.set(position, item);
    }
}
