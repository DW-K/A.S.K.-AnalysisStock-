package com.gachon.ask;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RankingFragment extends Fragment {
    RecyclerView RecyclerView;
    List<RankInfo> rankInfoList = new ArrayList<>();
    RankingAdapter adapter;
    //layout manager for recyclerview
    RecyclerView.LayoutManager layoutManager;
    TextView tv_rank;
    TextView tv_level;
    TextView tv_nickname;
    TextView tv_university;
    TextView tv_asset;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        View view = inflater.inflate(R.layout.fragment_ranking, container, false);

//        //initialize views
//        RecyclerView = view.findViewById(R.id.recycler_view_ranking);
//
//
//        rankInfoList.clear();
//
//
//
//        //set recycler view properties
//        RecyclerView.setHasFixedSize(true);
//        layoutManager = new LinearLayoutManager(getActivity());
//        RecyclerView.setLayoutManager(layoutManager);
//
//
//        showData();
        return view;

    }


    @Override
    public void onResume() {
        super.onResume();

        tv_rank  = getView().findViewById(R.id.tv_rank);
        tv_level  = getView().findViewById(R.id.tv_level);
        tv_nickname  = getView().findViewById(R.id.tv_nickname);
        tv_university  = getView().findViewById(R.id.tv_university);
        tv_asset  = getView().findViewById(R.id.tv_asset);

        //init firestore
        db = FirebaseFirestore.getInstance();

        rankInfoList.clear();

        //initialize views
        RecyclerView = getView().findViewById(R.id.recycler_view_ranking);

        //set recycler view properties
        RecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        RecyclerView.setLayoutManager(layoutManager);


        showData();

    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private void showData() {
        db.collection("user")
                .orderBy("userMoney", Query.Direction.DESCENDING) // show from the recent posts
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        //show data
                        for (DocumentSnapshot doc : task.getResult()) {
                            try{
                                RankInfo rankInfo= new RankInfo(
                                        doc.getLong("userRank").intValue(),
                                        doc.getLong("userLevel").intValue(),
                                        doc.getString("userNickName"),
                                        doc.getLong("userMoney").intValue());

                                rankInfoList.add(rankInfo);

                            } catch (RuntimeException e){
                                System.out.println(e);
                            }


                        }
                        Log.v("TAG", "랭킹에 나올 유저 개수:" + String.valueOf(rankInfoList.size()));


                        //adapter
                        adapter = new RankingAdapter(RankingFragment.this, rankInfoList);
                        //set adapter to recyclerview
                        RecyclerView.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //called when there is any error while retrieving
                        startToast(e.getMessage());
                    }
                });
    }

//
//    // 랭킹 정보 가져오기
//    private void setUserRanking(){
//        Firestore.getUserData(Auth.getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()){
//                    User user = task.getResult().toObject(User.class);
//                    tv_rank.setText(user.getUserRank()+" 위");
//                    tv_level.setText(user.getUserLevel());
//                    tv_nickname.setText(user.getUserNickName());
//                    tv_university.setText("가천대학교"); //edit
//                    tv_asset.setText(user.getUserMoney());
//                }else{
//                    Log.d("RankingFragment", "Fail to get the rank information.");
//                }
//            }
//        });
//    }

    private void startToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }


}
