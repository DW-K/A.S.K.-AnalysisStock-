package com.gachon.ask;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gachon.ask.base.BaseFragment;
import com.gachon.ask.community.CommunityCategoryActivity;
import com.gachon.ask.databinding.FragmentHomeBinding;
import com.gachon.ask.util.Auth;
import com.gachon.ask.util.Firestore;
import com.gachon.ask.util.Util;
import com.gachon.ask.util.model.Stock;
import com.gachon.ask.util.model.User;
import com.gachon.ask.xingapi.MainView;
import com.gachon.ask.StockActivity;
import com.gachon.ask.xingapi.sLoginSample1;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFragment extends BaseFragment<FragmentHomeBinding> implements HomeAdapter.onItemClickListener {
    private static final String TAG = "HomeFragment";
    private RecyclerView RecyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<Stock> myStockList;
    private HomeAdapter homeAdapter;

    private Boolean isScrolling = false;
    private Boolean isLastItemReached = false;

    private SwipeRefreshLayout swipeBoard = null;
    private DocumentSnapshot last;
    User level_user;
    static boolean isStockListExist;


    @Override
    protected FragmentHomeBinding getBinding() {
        return FragmentHomeBinding.inflate(getLayoutInflater());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        setUserMoney();
        setAdapter();
        setRefresh();
        // 모의투자 화면으로 이동!
        binding.buttonInvestment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("HomeFragment", "Invest Button pressed.");
                Intent intent = new Intent(getActivity(), MainView.class);
                startActivity(intent);
            }
        });
        return binding.getRoot();
    }


    @Override
    public void onResume() {
        super.onResume();
        //initialize views
        RecyclerView = getView().findViewById(R.id.home_board);

        //set recycler view properties
        RecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        RecyclerView.setLayoutManager(layoutManager);
        getInfoData();

        Button buttonStock = getView().findViewById(R.id.button_my_stock_temp);
        buttonStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("HomeFragment", "Invest Button pressed.");
                Intent intent = new Intent(getActivity(), SentimentReportActivity.class);
                intent.putExtra("stock_name","삼성전자");
                startActivity(intent);
            }
        });

        Button tempAddExpBtn =  getView().findViewById(R.id.btn_addExp);
        tempAddExpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LevelSystem lvlSystem = new LevelSystem();
                lvlSystem.addExp(level_user, 30);
                startToast("경험치를 30 추가했습니다. mypage에서 확인.");
            }
        });


    }

    // 홈 화면 자산 표시
    private void setUserMoney(){
        Firestore.getUserData(Auth.getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    User user = task.getResult().toObject(User.class);
                    if(user.getUserMoney() == 0){
                        binding.totalAssetsValue.setText("0 원");
                    }else{
                        binding.totalAssetsValue.setText(Util.toNumFormat(user.getUserMoney())+" 원");
                    }
                }else{
                    Log.d("MyPageFragment", "setUserMoney task is failed.");
                }
            }
        });
    }



    private void setAdapter() {
        Log.d("BoardFragment", "Set Adapter Run");
        myStockList = new ArrayList<>();
        Firestore.getUserData(Auth.getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    User user = task.getResult().toObject(User.class);
                    level_user = user;
                    myStockList = user.getMyStock();
                    if(myStockList != null && myStockList.size() > 0){
                        isStockListExist = true;
                        binding.buttonInvestment.setVisibility(View.GONE); // 주식 거래 내역이 있다면 모의투자 버튼 활성화 X

                        int sum_yield = 0;
                        for(int i = 0; i < myStockList.size(); i++){
                            if(myStockList.get(i).getStockNum().trim().equals("0")){
                                myStockList.remove(i); // 수량이 0이면 recyclerView에서 제거하여 보여줘야함.
                            }else{ // 어떤 종목이 매도되어서 수량이 0이라면 수익률을 계산 포함 X
                                sum_yield = sum_yield + Integer.parseInt(myStockList.get(i).getStockYield());
                            }
                        }
                        binding.totalProfitValue.setText(sum_yield+"%");
                        /* 총 수익률 업데이트 */
                        Firestore.updateProfitRate(Auth.getCurrentUser().getUid(), sum_yield).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                }else{
                                    Toast.makeText(getContext(),"총 수익률 업데이트 실패", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else if(myStockList.size() == 0){
                        binding.buttonInvestment.setVisibility(View.VISIBLE); // 주식 거래 내역이 없다면 모의투자 버튼 활성화 O
                        Toast.makeText(getContext(), "현재 주식 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                    // homeAdapter.notifyDataSetChanged();
                    // last
                }else{
                    Log.d("HomeFragment", "getUserData task is failed.");
                }

                //adapter
                homeAdapter = new HomeAdapter(getContext(),myStockList,HomeFragment.this);

                //set adapter to recyclerview
                RecyclerView.setAdapter(homeAdapter);
            }
        });
    }


    private void setRefresh() {
        binding.swipeBoard.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getInfoData();
            }
        });
    }

    private void getInfoData() {
        // homeAdapter.clear();
        // TODO : TYPE 설정

        Firestore.getUserData(Auth.getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    User user = task.getResult().toObject(User.class);
                    myStockList = user.getMyStock();
                    if(myStockList != null){ }
                    else{
                        Toast.makeText(getContext(), "현재 주식 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                    // homeAdapter.notifyDataSetChanged();
                    // last
                }else{
                    Log.d("HomeFragment", "getUserData task is failed.");
                }
                binding.swipeBoard.setRefreshing(false);
            }
        });
    }

    @Override
    public void onClick(View v, Stock myStockList) {

    }

    private void startToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
