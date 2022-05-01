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

import com.gachon.ask.settings.SettingActivity;
import com.gachon.ask.util.Auth;
import com.gachon.ask.util.CloudStorage;
import com.gachon.ask.util.Firestore;
import com.gachon.ask.util.model.Stock;
import com.gachon.ask.util.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class MyPageFragment extends Fragment {
    private User user;
    private ImageView iv_profile;
    private TextView tv_nickname, tv_level, tv_level_exp, tv_challenge_name, tv_challenge_detail, tv_challenge_date;
    private ImageButton btn_editProfile;
    private ProgressBar expBar;
    private ArrayList<Stock> myStockList;
    private LinearLayout no_challenge, recent_challenge;
    int level;

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

        btn_editProfile = view.findViewById(R.id.btn_editProfile);
        btn_editProfile.setOnClickListener(new View.OnClickListener() { // 설정 화면(SettingActivity)으로 이동
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });

        return view;
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


                    System.out.println("HomeFragment.isStockListExist: "+HomeFragment.isStockListExist);
                    if(HomeFragment.isStockListExist){
                        tv_challenge_name.setText(R.string.challenge_the_first_trade);
                        tv_challenge_detail.setText(R.string.challenge_the_first_trade_detail);
                        no_challenge.setVisibility(View.GONE);
                        recent_challenge.setVisibility(View.VISIBLE);
                        LocalDate today = LocalDate.now();
                        tv_challenge_date.setText(today.toString());

                    }else{
                        no_challenge.setVisibility(View.VISIBLE);
                        recent_challenge.setVisibility(View.GONE);
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
}