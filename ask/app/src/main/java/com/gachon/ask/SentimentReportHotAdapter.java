package com.gachon.ask;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.gachon.ask.databinding.ItemSentiReportHotkeywordBinding;

import java.util.ArrayList;

public class SentimentReportHotAdapter extends RecyclerView.Adapter<SentimentReportHotAdapter.SentiReportHotViewHolder>{

    private ArrayList<Post> myPostList;

    public SentimentReportHotAdapter(ArrayList<Post> myPostList) {
        this.myPostList = myPostList;
    }


    public static class SentiReportHotViewHolder extends RecyclerView.ViewHolder {
        private ItemSentiReportHotkeywordBinding itemsentireporthotkeywordBinding;

        public SentiReportHotViewHolder(@NonNull ItemSentiReportHotkeywordBinding binding) {
            super(binding.getRoot());
            this.itemsentireporthotkeywordBinding = binding;
        }
    }

    @NonNull
    @Override
    public SentimentReportHotAdapter.SentiReportHotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SentimentReportHotAdapter.SentiReportHotViewHolder(ItemSentiReportHotkeywordBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SentiReportHotViewHolder holder, int position) {
        ItemSentiReportHotkeywordBinding binding = holder.itemsentireporthotkeywordBinding;
        Post myPost = myPostList.get(position);
        binding.tvRank.setText(myPost.getNewsCountId()+""); // Int->String 변환 필요해보임
        binding.tvKeyword.setText(myPost.getWord());

        if(Double.parseDouble(myPost.getPositive()) > 0.5) { // 긍정의 비율이 더 높다면 긍정으로 표시.
            binding.tvSentiment.setText("긍정");
        }else{
            binding.tvSentiment.setText("부정");
        }
    }

    @Override
    public int getItemCount() {
        return myPostList.size();
    }

    public void clear() {
        myPostList.clear();
        notifyDataSetChanged();
    }

}
