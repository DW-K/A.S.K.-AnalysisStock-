package com.gachon.ask;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gachon.ask.databinding.ItemHomeStockBinding;
import com.gachon.ask.util.model.Stock;
import com.gachon.ask.xingapi.MainView;
import com.gachon.ask.xingapi.sLoginSample1;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder>{
    private Context context;
    private ArrayList<Stock> myStockList;

    public interface onItemClickListener {
        void onClick(View v, Stock myStockList);
    }

    private onItemClickListener listener = null;

    public HomeAdapter(Context context, ArrayList<Stock> myStockList, onItemClickListener listener) {
        this.context = context;
        this.myStockList = myStockList;
        this.listener = listener;
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder {
        private ItemHomeStockBinding itemHomeStockBinding;

        public HomeViewHolder(@NonNull ItemHomeStockBinding binding) {
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
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeViewHolder(ItemHomeStockBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
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
