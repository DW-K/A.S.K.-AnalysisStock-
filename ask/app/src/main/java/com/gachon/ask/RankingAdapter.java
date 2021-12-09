package com.gachon.ask;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gachon.ask.community.CategoryActivity;
import com.gachon.ask.community.PostViewActivity;
import com.gachon.ask.util.model.User;
import com.google.firebase.Timestamp;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class RankingAdapter extends RecyclerView.Adapter<ViewHolderRanking> {
    RankingFragment rankingFragment;
    List<RankInfo> rankInfoList;

    public RankingAdapter(RankingFragment rankingFragment, List<RankInfo> rankInfoList) {
        this.rankingFragment = rankingFragment;
        this.rankInfoList = rankInfoList;
    }


    @NonNull
    @Override
    public ViewHolderRanking onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate layout
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.ranking_view_layout, viewGroup,false);

        ViewHolderRanking viewHolderRanking = new ViewHolderRanking(itemView);


//        // item clicked
//        viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//
//                Intent intent = new Intent(view.getContext(), PostViewActivity.class);
//                intent.putExtra("rank",user.get(position).getNickname());
//                intent.putExtra("level",user.get(position).getContents());
//                intent.putExtra("nickname",user.get(position).getPublisher());
//                intent.putExtra("university",user.get(position).getCategory());
//                intent.putExtra("asset",user.get(position).getCreatedAt());
//
//
//                categoryActivity.startActivity(intent);
//
//            }
//
//            @Override
//            public void onItemLongClick(View view, int position) {
//
//            }
//
//        });


        return viewHolderRanking;
    }

    DecimalFormat myFormatter = new DecimalFormat("###,###");

    @Override
    public void onBindViewHolder(@NonNull ViewHolderRanking holder, int i) {


        //bind views
        holder.vRank.setText(rankInfoList.get(i).getUserRank().toString() + " 위");
        holder.vLevel.setText("Lv. " + rankInfoList.get(i).getUserLevel().toString());
        holder.vNickname.setText(rankInfoList.get(i).getNickname());
        holder.vAsset.setText(myFormatter.format(rankInfoList.get(i).getUserMoney()) + "원");
    }

    @Override
    public int getItemCount() {
        return rankInfoList.size();
    }


}
