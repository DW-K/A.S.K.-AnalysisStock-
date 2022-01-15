package com.gachon.ask.community;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gachon.ask.R;
import com.gachon.ask.WriteInfo;
import com.gachon.ask.WritingActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class CommunityCategoryActivity extends AppCompatActivity {
    public static Context mContext;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<WriteInfo> writeInfoList = new ArrayList<>();
    RecyclerView mRecyclerView;
    //layout manager for recyclerview
    RecyclerView.LayoutManager layoutManager;
    String category;
    TextView categoryName;
    Button btnAddPost;
    TextView tv_no_post;
    PostViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_list);
        mContext = this;
        Intent intent = getIntent();
        category = intent.getStringExtra("category");
        categoryName = findViewById(R.id.category_name);
        categoryName.setText(category);
        tv_no_post = findViewById(R.id.tv_no_post);
        tv_no_post.setText(R.string.no_post);



        showData();

        btnAddPost = findViewById(R.id.btn_add_post);
        btnAddPost.setOnClickListener(view -> {
            Intent addPostIntent = new Intent(getApplicationContext(), WritingActivity.class);
            addPostIntent.putExtra("category", category);
            startActivity(addPostIntent);
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }


    private void showData() {
        System.out.println("show data 실행함");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Posts")
                .orderBy("createdAt", Query.Direction.DESCENDING) // show from the recent posts
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        //show data
                        for (DocumentSnapshot doc : task.getResult()) {
                            try{
                                WriteInfo writeInfo = new WriteInfo(
                                        doc.getString("posts_id"),
                                        doc.getString("nickname"),
                                        doc.getString("contents"),
                                        doc.getString("publisher"),
                                        doc.getString("category"),
                                        doc.getTimestamp("createdAt"),
                                        doc.getLong("num_heart").intValue(),
                                        doc.getLong("num_comment").intValue(),
                                        (ArrayList<String>) doc.get("userlist_heart"));


                                if(writeInfo.getCategory().equals(category)) {
                                    writeInfoList.add(writeInfo);
                                }

                            } catch (RuntimeException e){
                                System.out.println(e);
                            }


                        }
                        Log.v("TAG", String.valueOf(writeInfoList.size()));


                        // 아무것도 없을때 없다고 표시하기
//                        if(String.valueOf(writeInfoList.size()).equals('0')){
//                            tv_no_post.bringToFront();
//                            tv_no_post.setVisibility(View.VISIBLE);
//                        }else{
//                            tv_no_post.setVisibility(View.INVISIBLE);
//                        }

                        //adapter
                        adapter = new PostViewAdapter();
                        //set adapter to recyclerview
                        mRecyclerView.setAdapter(adapter);
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


    @Override
    public void onResume() {
        super.onResume();
        setContentView(R.layout.activity_community_list);


        Intent intent = getIntent();
        category = intent.getStringExtra("category");
        categoryName = findViewById(R.id.category_name);
        categoryName.setText(category);


        btnAddPost = findViewById(R.id.btn_add_post);
        btnAddPost.setOnClickListener(view -> {
            Intent addPostIntent = new Intent(getApplicationContext(), WritingActivity.class);
            addPostIntent.putExtra("category", category);
            startActivity(addPostIntent);
        });


        //init firestore
        db = FirebaseFirestore.getInstance();

        writeInfoList.clear();
        //initialize views
        mRecyclerView = findViewById(R.id.recycler_view);

        //set recycler view properties
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        showData();

    }

    private void startMyActivity(Class c){
        Intent intent = new Intent(this,c);
        startActivity(intent);
    }
    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}