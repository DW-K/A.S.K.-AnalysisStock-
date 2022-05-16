package com.gachon.ask;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gachon.ask.databinding.ItemHomeStockBinding;
import com.gachon.ask.util.model.Stock;
import com.gachon.ask.xingapi.MainView;

import java.util.ArrayList;

public class MyPageAdapter extends RecyclerView.Adapter<MyPageAdapter.MyPageViewHolder>{
    private Context context;
    private ArrayList<Stock> myStockList;

    public interface onItemClickListener {
        void onClick(View v, Stock myStockList);
    }

    private onItemClickListener listener = null;

    public MyPageAdapter(Context context, ArrayList<Stock> myStockList, MyPageAdapter.onItemClickListener listener) {
        this.context = context;
        this.myStockList = myStockList;
        this.listener = listener;
    }

    public class MyPageViewHolder extends RecyclerView.ViewHolder{
        private ItemHomeStockBinding itemHomeStockBinding;

        public MyPageViewHolder(@NonNull ItemHomeStockBinding binding) {
            super(binding.getRoot());
            this.itemHomeStockBinding = binding;

            itemHomeStockBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 주식 리스트 클릭하면 모의투자 로그인 화면으로 이동
                    listener.onClick(v, myStockList.get(getAdapterPosition()));
                    Intent intent = new Intent(v.getContext(), MainView.class);
                    context.startActivity(intent);
                }
            });
        }
    }

    @NonNull
    @Override
    public MyPageAdapter.MyPageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyPageAdapter.MyPageViewHolder(ItemHomeStockBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyPageViewHolder holder, int position) {
        ItemHomeStockBinding binding = holder.itemHomeStockBinding;
        Stock myStock = myStockList.get(position);
        binding.tvStockName.setText(myStock.getStockName());
        myStock.setStockNum(myStock.getStockNum().replace(" ",""));
        binding.tvStockNum.setText(myStock.getStockNum()+"주");
        binding.tvStockPrice.setText(myStock.getStockPrice()+"원");
        if(Integer.parseInt(myStock.getStockYield()) > 0){
            binding.tvStockYield.setText("+"+myStock.getStockYield()+"%");
        }else{
            binding.tvStockYield.setText(myStock.getStockYield()+"%");
        }
    }

    @Override
    public int getItemCount() {
        // 새로 가입한 계정의 경우 이 부분에서 null 에러 발생
        return myStockList.size();
    }

    public void clear() {
        myStockList.clear();
        notifyDataSetChanged();
    }
}
