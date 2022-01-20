package com.gachon.ask;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gachon.ask.community.PostViewActivity;

public class ViewHolder extends RecyclerView.ViewHolder {
    TextView vContents, vNickname, vUploadTime, vComment, vHeart;
    View mView;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);

        mView = itemView;

        //item click
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v, getAdapterPosition());
                Intent intent = new Intent(itemView.getContext(), PostViewActivity.class);
            }
        });

        //item long click listener
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mClickListener.onItemLongClick(v, getAdapterPosition());
                return false;
            }
        });


        //initialize views with post_view_layout.xml

        vContents = itemView.findViewById(R.id.tv_contents);
        vNickname = itemView.findViewById(R.id.tv_nickname);
        vUploadTime = itemView.findViewById(R.id.tv_created_At);
        vHeart = itemView.findViewById(R.id.tv_heart);
        vComment = itemView.findViewById(R.id.tv_comment);
    }

    private ClickListener mClickListener;

    //interface for click listener
    public interface ClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);

    }

    public void setOnClickListener(ClickListener clickListener) {
        mClickListener = clickListener;
    }

}
