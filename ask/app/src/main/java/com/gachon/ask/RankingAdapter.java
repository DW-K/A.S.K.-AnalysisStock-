package com.gachon.ask;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.rpc.context.AttributeContext;

import java.util.ArrayList;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder> {
    ItemClickListener itemClickListener;
    ArrayList<RankInfo> items = new ArrayList<RankInfo>();
    private static final String TAG = "RankingAdapter";
    private FirebaseUser user;
    private Context context;

    public RankingAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.item_ranking, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        RankInfo item = items.get(position);
        viewHolder.setItem(item);


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_change, iv_profile, iv_univ;
        TextView tv_rank, tv_rankChange, tv_level, tv_nickname, tv_yield;
        int item_pos;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            tv_rank = (TextView) itemView.findViewById(R.id.tv_rank);
//            iv_change = itemView.findViewById(R.id.iv_change);
            iv_profile = itemView.findViewById(R.id.iv_profile);
//            tv_rankChange = (TextView) itemView.findViewById(R.id.tv_change);
            tv_level = (TextView) itemView.findViewById(R.id.tv_level);
            tv_nickname = (TextView) itemView.findViewById(R.id.tv_nickname);
            tv_yield = (TextView) itemView.findViewById(R.id.tv_yield);

            iv_univ= itemView.findViewById(R.id.iv_univ);
            user = FirebaseAuth.getInstance().getCurrentUser();

            item_pos = getAdapterPosition() + 1;

            // 추후 click event (프로필 이동 등)
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext() , "click한 위치의 item_pos: "+item_pos , Toast.LENGTH_SHORT).show();
                }
            });

        }



        public void setItem(RankInfo item) {
            int rank = item.getuNewRank();
//            int uRankChange = item.getuLastRank() - rank;
//
//            // 순위 변동에 따라 화살표 그림 바꾸기
//            int iv_rankChange_name;
//            if(uRankChange > 0) iv_rankChange_name = R.drawable.outline_arrow_drop_up_24;
//            else if(uRankChange < 0) iv_rankChange_name = R.drawable.outline_arrow_drop_down_24;
//            else iv_rankChange_name = R.drawable.outline_remove_24;
//
//            iv_change.setImageResource(iv_rankChange_name);
//
//            uRankChange = Math.abs(uRankChange);
            tv_rank.setText(rank +" 위"); // 순위 ex) 1위
//            tv_rankChange.setText(String.valueOf(uRankChange)); // 변동 ex) 4
            tv_level.setText(context.getResources().getString(R.string.level) + String.valueOf(item.getuLevel())); // 레벨 ex) Lv.3
            tv_yield.setText(String.valueOf(item.getuYield())); // 수익률 ex) 32 %
            tv_nickname.setText(String.valueOf(item.getuNickname())); // 유저닉네임 ex) 민하



        }
    }


    public void addItem(RankInfo item) {
        items.add(item);
    }

    public void setItems(ArrayList<RankInfo> items) {
        this.items = items;
    }

    public RankInfo getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, RankInfo item) {
        items.set(position, item);
    }

    public interface ItemClickListener
    {
        void onItemClick(int position);
    }

    public void StartToast(Integer msg){
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }
}

