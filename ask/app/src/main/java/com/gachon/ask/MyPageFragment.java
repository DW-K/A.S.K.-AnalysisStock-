package com.gachon.ask;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.gachon.ask.util.Auth;
import com.gachon.ask.util.Firestore;
import com.gachon.ask.util.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class MyPageFragment extends Fragment {

    private TextView tv_nickname;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);
        tv_nickname = view.findViewById(R.id.username_title);
        setUserNickName();
        return view;
    }

    // 프로필 닉네임 표시
    private void setUserNickName(){
        Firestore.getUserData(Auth.getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    User user = task.getResult().toObject(User.class);
                    tv_nickname.setText(user.getUserNickName()+"님");
                }else{
                    Log.d("MyPageFragment", "setUserNick task is failed.");
                }
            }
        });
    }

}