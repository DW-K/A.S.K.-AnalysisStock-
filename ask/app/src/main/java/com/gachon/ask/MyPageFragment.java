package com.gachon.ask;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.gachon.ask.util.Auth;
import com.gachon.ask.util.Firestore;
import com.gachon.ask.util.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class MyPageFragment extends Fragment {
    private User user;
    private TextView tv_nickname, tv_level, tv_level_exp;
    private ProgressBar expBar;
    int level;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);
        tv_nickname = view.findViewById(R.id.username_title);
        tv_level = view.findViewById(R.id.userlevel_text);
        setUserData();
        return view;
    }

    // 프로필  표시
    private void setUserData(){
        Firestore.getUserData(Auth.getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    user = task.getResult().toObject(User.class);
                    tv_nickname.setText(user.getUserNickName()+"님");
                    level = user.getUserLevel();
                    tv_level.setText(getString(R.string.level) + user.getUserLevel());
                    tv_level_exp.setText(user.getUserExp()+" %");
                    expBar.setProgress(user.getUserExp());
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
        expBar = getView().findViewById(R.id.progressBar);
        setUserData();

        Button tempAddExpBtn =  getView().findViewById(R.id.btn_addExp);
        tempAddExpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LevelSystem lvlSystem = new LevelSystem();
                int exp = lvlSystem.addExp(user, 30);
                tv_level_exp.setText(exp + " %");
                tv_level.setText(getString(R.string.level) + lvlSystem.level);
                expBar.setProgress(exp);
            }
        });

    }
}