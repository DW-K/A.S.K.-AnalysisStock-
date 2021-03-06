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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomeFragment extends BaseFragment<FragmentHomeBinding> implements HomeAdapter.onItemClickListener, HomeHotAdapter.onItemClickListener {
    private static final String TAG = "HomeFragment";
    private RecyclerView RecyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private RecyclerView RecyclerView2;
    private RecyclerView.LayoutManager layoutManager2;

    private ArrayList<Stock> myStockList;
    private ArrayList<Post> myPostList;
    private HomeAdapter homeAdapter;
    private HomeHotAdapter homeHotAdapter;

    private Boolean isScrolling = false;
    private Boolean isLastItemReached = false;

    private SwipeRefreshLayout swipeBoard = null;
    private DocumentSnapshot last;

    private int itemCount;
    private ArrayList<String> keywordLists;
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
        getKeywordData();
        // ???????????? ???????????? ??????!
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

        //initialize views 2
        RecyclerView2 = getView().findViewById(R.id.home_board2);

        //set recycler view properties 2
        RecyclerView2.setHasFixedSize(true);
        layoutManager2 = new LinearLayoutManager(getActivity());
        RecyclerView2.setLayoutManager(layoutManager2);

        getInfoData();
        setAdapter();
        Button buttonStock = getView().findViewById(R.id.button_my_stock_temp);
        buttonStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("HomeFragment", "Invest Button pressed.");
                Intent intent = new Intent(getActivity(), SentimentReportActivity.class);
                intent.putExtra("stock_name","?????????");
                startActivity(intent);
            }
        });


    }

    // ??? ?????? ?????? ??????
    private void setUserMoney(){
        Firestore.getUserData(Auth.getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    User user = task.getResult().toObject(User.class);
                    if(user.getUserMoney() == 0){
                        binding.totalAssetsValue.setText("0 ???");
                    }else{
                        binding.totalAssetsValue.setText(Util.toNumFormat(user.getUserMoney())+" ???");
                    }
                }else{
                    Log.d("MyPageFragment", "setUserMoney task is failed.");
                }
            }
        });
    }



    private void setAdapter() {
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
                        binding.buttonInvestment.setVisibility(View.GONE); // ?????? ?????? ????????? ????????? ???????????? ?????? ????????? X

                        int sum_yield = 0;
                        for(int i = 0; i < myStockList.size(); i++){
                            if(myStockList.get(i).getStockNum().trim().equals("0")){
                                myStockList.remove(i); // ????????? 0?????? recyclerView?????? ???????????? ???????????????.
                            }else{ // ?????? ????????? ??????????????? ????????? 0????????? ???????????? ?????? ?????? X
                                sum_yield = sum_yield + Integer.parseInt(myStockList.get(i).getStockYield());
                            }
                        }
                        binding.totalProfitValue.setText(sum_yield+"%");
                        /* ??? ????????? ???????????? */
                        Firestore.updateProfitRate(Auth.getCurrentUser().getUid(), sum_yield).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                }else{
                                    Toast.makeText(getContext(),"??? ????????? ???????????? ??????", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else if(myStockList.size() == 0){
                        binding.buttonInvestment.setVisibility(View.VISIBLE); // ?????? ?????? ????????? ????????? ???????????? ?????? ????????? O
                        Toast.makeText(getContext(), "?????? ?????? ???????????? ????????????.", Toast.LENGTH_SHORT).show();
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
        // TODO : TYPE ??????

        Firestore.getUserData(Auth.getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    User user = task.getResult().toObject(User.class);
                    myStockList = user.getMyStock();
                    if(myStockList != null){ }
                    else{
                        Toast.makeText(getContext(), "?????? ?????? ???????????? ????????????.", Toast.LENGTH_SHORT).show();
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

    public void getKeywordData() {
        String SERVER_URL = BuildConfig.SERVER;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHOlderApi jsonPlaceHOlderApi = retrofit.create(JsonPlaceHOlderApi.class);

        Call<List<Post>> call = jsonPlaceHOlderApi.getNewsCount();
        call = jsonPlaceHOlderApi.getNewsCount();
        myPostList = new ArrayList<>();
        keywordLists = new ArrayList<>();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                myPostList.clear();
                keywordLists.clear();
                itemCount = 0;
                if (!response.isSuccessful()) return;

                List<Post> posts = response.body();
                String[] badKeyword = {"??????","??????","??????","??????","??????","?????????","??????","??????", "?????????", "??????",
                        "21???", "??????", "??????", "??????", "17???", "??????", "??????", "??????", "?????????", "4???", "5???",
                        "7???", "10???", "??????", "13???", "14???", "15???", "18???", "19???", "20???", "22???", "3???", "24???", "26???", "28???", "29???", "??????", "1???"};
                ArrayList<String>  badKeywords = new ArrayList<>(Arrays.asList(badKeyword));



                for ( Post post : posts) {

                    String keyword = post.getWord();

                    if(!keywordLists.contains(keyword) && !badKeywords.contains(keyword)){
                        myPostList.add(post);
                        keywordLists.add(keyword);
                        itemCount+=1;
                    }
                    if(itemCount>=10) {
                        break;
                    }
                }

                // adapter
                homeHotAdapter = new HomeHotAdapter(getContext(), myPostList, HomeFragment.this);
                // set adapter to recyclerview
                RecyclerView2.setAdapter(homeHotAdapter);
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                System.out.println("??????????????????.");
            }

        });
    }

    @Override
    public void onClick(View v, Stock myStockList) {

    }

    private void startToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v, Post myPostList) {

    }
}
