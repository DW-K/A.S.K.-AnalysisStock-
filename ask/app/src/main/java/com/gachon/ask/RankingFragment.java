package com.gachon.ask;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gachon.ask.util.Firestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class RankingFragment extends Fragment {
    private static RankingAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    FirebaseUser user;
    int item_count;
    String selected_rank_category = "profitRate";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipe_view_ranking);
        user = FirebaseAuth.getInstance().getCurrentUser();

        // initialize views
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_ranking);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        view.findViewById(R.id.btn_ranking_yield).setOnClickListener(onClickListener);
        view.findViewById(R.id.btn_ranking_level).setOnClickListener(onClickListener);
        return view;
    }

    // 버튼 클릭 이벤트 작성
    View.OnClickListener onClickListener = v -> {
        switch (v.getId()) {
            case R.id.btn_ranking_yield:
                selected_rank_category = "profitRate";
                break;
            case R.id.btn_ranking_level:
                selected_rank_category = "userLevel";
                break;
        }
        refresh(selected_rank_category);
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh(selected_rank_category);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh(selected_rank_category);
    }

    public void refresh(String current_category) {

        adapter = new RankingAdapter(getContext());
        item_count = adapter.getItemCount();
        adapter.notifyDataSetChanged();
        showData(adapter, current_category);
    }

    private void showData(RankingAdapter adapter, String category) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("user")
                .orderBy(category, Query.Direction.DESCENDING) // 선택한 카테고리(수익률, 변동)에 맞게 내림차순
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        StartToast(category + "순으로 정렬되었습니다.");
                        int position = 0;
                        // show data
                        for (DocumentSnapshot doc : task.getResult()) {
                            position += 1;
                            System.out.print("position : " + position);
                            try {
                                String uid = doc.getString("uid");
                                Integer uLevel = doc.getLong("userLevel").intValue();
                                String uNickname = doc.getString("userNickName");
                                Integer uYield = doc.getLong("profitRate").intValue();
                                String uProfileImg = doc.getString("userProfileImgURL");

                                // position 기반으로 rank 재설정
                                int uRank = position;

                                user = FirebaseAuth.getInstance().getCurrentUser();
                                adapter.addItem(new RankInfo(uRank, uLevel, uNickname, uProfileImg, uYield));

                                // set adapter to recyclerview
                                recyclerView.setAdapter(adapter);

                                // 파이어스토어에 rank 업데이트
                                updateUserRank(uid, uRank);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
    }

    public void updateUserRank(String uid, int userRank) {
        int userLastRank = userRank; // lastRank를 현재의 새로운 rank로 변경

        // 입력받은 uid의 데이터를 업데이트
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("user").document(uid);
        // userLastRank
        docRef
                .update("userLastRank", userLastRank)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + userLastRank);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating post id", e);
                    }
                });
    }

    public void StartToast(String msg) {
        Toast toast = Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT);
        toast.show();
    }
}