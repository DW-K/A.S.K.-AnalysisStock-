package com.gachon.ask;



import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class ViewHolderRanking extends RecyclerView.ViewHolder {
    TextView vRank, vLevel, vNickname, vUniversity, vAsset;
    View mView;

    public ViewHolderRanking(@NonNull View itemView) {
        super(itemView);

        mView = itemView;

//        //item click
//        itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mClickListener.onItemClick(v, getAdapterPosition());
//                Intent intent = new Intent(itemView.getContext(), PostViewDetailActivity.class);
//            }
//        });
//
//        //item long click listener
//        itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                mClickListener.onItemLongClick(v, getAdapterPosition());
//                return false;
//            }
//        });


        //initialize views with ranking_detail_layout.xml

        vRank = itemView.findViewById(R.id.tv_rank);
        vLevel = itemView.findViewById(R.id.tv_level);
        vNickname = itemView.findViewById(R.id.tv_nickname);
        vUniversity = itemView.findViewById(R.id.tv_university);
        vAsset = itemView.findViewById(R.id.tv_asset);
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
