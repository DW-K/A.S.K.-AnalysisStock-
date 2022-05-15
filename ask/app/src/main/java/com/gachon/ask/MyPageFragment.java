package com.gachon.ask;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gachon.ask.base.BaseFragment;
import com.gachon.ask.databinding.FragmentMypageBinding;
import com.gachon.ask.settings.SettingActivity;
import com.gachon.ask.util.Auth;
import com.gachon.ask.util.CloudStorage;
import com.gachon.ask.util.Firestore;
import com.gachon.ask.util.model.Stock;
import com.gachon.ask.util.model.User;
import com.gachon.ask.xingapi.MainView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.time.LocalDate;
import java.util.ArrayList;

public class MyPageFragment extends BaseFragment<FragmentMypageBinding> implements MyPageAdapter.onItemClickListener {
    private static final String TAG = "MyPageFragment";
    private androidx.recyclerview.widget.RecyclerView RecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MyPageAdapter myPageAdapter;
    static boolean isStockListExist;

    private User user;
    private ImageView iv_profile;
    private TextView tv_nickname, tv_level, tv_level_exp, tv_challenge_name, tv_challenge_detail, tv_challenge_date;
    private ImageButton btn_editProfile;
    private ProgressBar expBar;
    private ArrayList<Stock> myStockList;
    private LinearLayout no_challenge, recent_challenge;
    int level;

    @Override
    protected FragmentMypageBinding getBinding() {
        return FragmentMypageBinding.inflate(getLayoutInflater());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);
        tv_nickname = view.findViewById(R.id.username_title);
        tv_challenge_name = view.findViewById(R.id.tv_challenge);
        tv_challenge_detail = view.findViewById(R.id.tv_challenge_detail);
        tv_level = view.findViewById(R.id.userlevel_text);
        iv_profile = view.findViewById(R.id.mypage_user_image);
        no_challenge = view.findViewById(R.id.no_challenge);
        recent_challenge = view.findViewById(R.id.challenge_item);
        tv_challenge_date = view.findViewById(R.id.achievement_acquisition_date);

        setUserData();
        setAdapter();
        setRefresh();

        binding.btnEditProfile.setOnClickListener(new View.OnClickListener() { // 설정 화면(SettingActivity)으로 이동
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });

        // 모의투자 화면으로 이동!
        binding.buttonInvestment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("MyPageFragment", "Invest Button pressed.");
                Intent intent = new Intent(getActivity(), MainView.class);
                startActivity(intent);
            }
        });

        return binding.getRoot();
    }

    // 프로필  표시
    private void setUserData(){
        myStockList = new ArrayList<>();
        Firestore.getUserData(Auth.getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    user = task.getResult().toObject(User.class);
                    tv_nickname.setText(user.getUserNickName()+"님");
                    level = user.getUserLevel();
                    tv_level.setText(getString(R.string.level) + user.getUserLevel());
                    tv_level_exp.setText(user.getUserExp()+" %");
                    expBar.setProgress(user.getUserExp());


                    System.out.println("MyPageFragment.isStockListExist: "+MyPageFragment.isStockListExist);
                    if(MyPageFragment.isStockListExist){
                        binding.tvChallenge.setText(R.string.challenge_the_first_trade);
                        binding.tvChallengeDetail.setText(R.string.challenge_the_first_trade_detail);
                        binding.noChallenge.setVisibility(View.GONE);
                        binding.challengeItem.setVisibility(View.VISIBLE);
                        LocalDate today = LocalDate.now();
                        binding.achievementAcquisitionDate.setText(today.toString());

                    }else{
                        binding.noChallenge.setVisibility(View.VISIBLE);
                        binding.challengeItem.setVisibility(View.GONE);
                    }

                    if(user.getUserProfileImgURL() != null) {
                        CloudStorage.getImageFromURL(user.getUserProfileImgURL()).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                            @Override
                            public void onComplete(@NonNull Task<byte[]> task) {
                                if(task.isSuccessful()) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                    iv_profile.setImageBitmap(bitmap);
                                }
                            }
                        });
                    }
                    else {
                        Log.d(MyPageFragment.this.getClass().getSimpleName(), "Profile Image NULL");
                    }
                }else{
                    Log.d("MyPageFragment", "setUserNick task is failed.");
                }
            }
        });
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
        setAdapter();
        tv_nickname =  getView().findViewById(R.id.username_title);
        tv_level =  getView().findViewById(R.id.userlevel_text);
        tv_level_exp =  getView().findViewById(R.id.level_exp);
        iv_profile = getView().findViewById(R.id.mypage_user_image);
        expBar = getView().findViewById(R.id.progressBar);
        setUserData();

        /* 경험치 임시 추가 버튼 */
//        Button tempAddExpBtn =  getView().findViewById(R.id.btn_addExp);
//        tempAddExpBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LevelSystem lvlSystem = new LevelSystem();
//                int exp = lvlSystem.addExp(user, 30);
//                tv_level_exp.setText(exp + " %");
//                tv_level.setText(getString(R.string.level) + lvlSystem.level);
//                expBar.setProgress(exp);
//            }
//        });

    }

    private void setAdapter() {
        Log.d("BoardFragment", "Set Adapter Run");
        myStockList = new ArrayList<>();
        Firestore.getUserData(Auth.getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    User user = task.getResult().toObject(User.class);
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
                    // MyPageAdapter.notifyDataSetChanged();
                    // last
                }else{
                    Log.d("MyPageFragment", "getUserData task is failed.");
                }

                //adapter
                myPageAdapter = new MyPageAdapter(getContext(),myStockList, MyPageFragment.this);

                //set adapter to recyclerview
                RecyclerView.setAdapter(myPageAdapter);
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
                    Log.d("MyPageFragment", "getUserData task is failed.");
                }
                binding.swipeBoard.setRefreshing(false);
            }
        });
    }

    @Override
    public void onClick(View v, Stock myStockList) {

    }
}