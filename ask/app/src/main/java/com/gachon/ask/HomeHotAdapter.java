package com.gachon.ask;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.gachon.ask.databinding.ItemHotkeywordBinding;
import com.gachon.ask.util.model.Stock;

import java.util.ArrayList;
import java.util.List;

public class HomeHotAdapter extends RecyclerView.Adapter<HomeHotAdapter.HomeHotViewHolder>{
    private Context context;
    private ArrayList<Post> myPostList;

    public interface onItemClickListener {
        void onClick(View v, Post myPostList);
    }

    private onItemClickListener listener = null;

    public HomeHotAdapter(Context context, ArrayList<Post> myPostList, HomeHotAdapter.onItemClickListener listener) {
        this.context = context;
        this.myPostList = myPostList;
        this.listener = listener;
    }


    public static class HomeHotViewHolder extends RecyclerView.ViewHolder {
        private ItemHotkeywordBinding itemHotkeywordBinding;

        public HomeHotViewHolder(@NonNull ItemHotkeywordBinding binding) {
            super(binding.getRoot());
            this.itemHotkeywordBinding = binding;
        }
    }

    @NonNull
    @Override
    public HomeHotAdapter.HomeHotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeHotAdapter.HomeHotViewHolder(ItemHotkeywordBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HomeHotViewHolder holder, int position) {
        ItemHotkeywordBinding binding = holder.itemHotkeywordBinding;
        Post myPost = myPostList.get(position);
        binding.tvRank.setText((position+1)+"");
        binding.tvKeyword.setText(myPost.getWord());
        binding.tvCompany.setText(myPost.getCompany());
        if(Double.parseDouble(myPost.getPositive()) > 0.5) { // 긍정의 비율이 더 높다면 긍정으로 표시.
            binding.tvSentiment.setText("긍정");
            Log.d("HomeHotAdapter.java","TEST1");
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
