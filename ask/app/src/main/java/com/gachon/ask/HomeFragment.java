package com.gachon.ask;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.gachon.ask.util.Auth;
import com.gachon.ask.util.Firestore;
import com.gachon.ask.util.Util;
import com.gachon.ask.util.model.User;
import com.gachon.ask.xingapi.MainView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class HomeFragment extends Fragment {

    private TextView tv_total_assets;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        Button btnInvest = (Button) view.findViewById(R.id.button_investment);
        tv_total_assets = (TextView) view.findViewById(R.id.total_assets_value);
        // 모의투자 화면으로 이동!
        btnInvest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("HomeFragment", "Invest Button pressed.");
                Intent intent = new Intent(getActivity(), MainView.class);
                startActivity(intent);
            }
        });
        setUserMoney();
        return view;
    }

    // 홈 화면 자산 표시
    private void setUserMoney(){
        Firestore.getUserData(Auth.getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    User user = task.getResult().toObject(User.class);
                    if(user.getUserMoney() == 0){
                        tv_total_assets.setText("0 원");
                    }else{
                        tv_total_assets.setText(Util.toNumFormat(user.getUserMoney())+" 원");
                    }
                }else{
                    Log.d("MyPageFragment", "setUserMoney task is failed.");
                }
            }
        });
    }


}
