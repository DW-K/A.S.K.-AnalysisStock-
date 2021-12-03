package com.gachon.ask.community;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gachon.ask.CustomAdapter;
import com.gachon.ask.R;
import com.gachon.ask.WriteInfo;
import com.gachon.ask.WritingActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class CategoryActivity extends AppCompatActivity {
//    private FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
    public static Context mContext;

    List<WriteInfo> writeInfoList = new ArrayList<>();
    RecyclerView mRecyclerView;
    //layout manager for recyclerview
    RecyclerView.LayoutManager layoutManager;
    String category;
    TextView categoryName;
    Button btnAddPost;

    CustomAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_list);

        Intent intent = getIntent();
        category = intent.getStringExtra("category");
        categoryName = findViewById(R.id.category_name);
        categoryName.setText(category); //이;게 왜 안먹혀,,,,



        btnAddPost = findViewById(R.id.btn_add_post);
        btnAddPost.setOnClickListener(view -> {
            Intent addPostIntent = new Intent(getApplicationContext(), WritingActivity.class);
            addPostIntent.putExtra("category", category);
            startActivity(addPostIntent);
//            startMyActivity(WritingActivity.class);
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        startMyActivity(CommunityFragment.class);
        finish();
    }


    FirebaseFirestore db = FirebaseFirestore.getInstance();
//    user = FirebaseAuth.getInstance().getCurrentUser();
//    private void showData(int flag, String[] result) {
//        db.collection("Posts")
//                .orderBy("createdAt", Query.Direction.DESCENDING) // show from the recent posts
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//
//                        //show data
//                        for (DocumentSnapshot doc : task.getResult()) {
//
//                            WriteInfo writeInfo = new WriteInfo(
//                                    doc.getString("posts_id"),
//                                    doc.getString("nickname"),
////                                    doc.getString("title"),
//                                    doc.getString("contents"),
//                                    doc.getString("publisher"),
//                                    doc.getString("category"),
//                                    doc.getTimestamp("createdAt"));
//
//
//                        }
//
//                        //adapter
//                        adapter = new CustomAdapter(CategoryActivity.this, writeInfoList);
//                        //set adapter to recyclerview
//                        mRecyclerView.setAdapter(adapter);
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        //called when there is any error while retrieving
//                        startToast(e.getMessage());
//                    }
//                });
//    }
//

    @Override
    public void onResume() {
        super.onResume();
        setContentView(R.layout.activity_community_list);

//        //init firestore
//        db = FirebaseFirestore.getInstance();
//
//        writeInfoList.clear();
//        //initialize views
//        mRecyclerView = findViewById(R.id.recycler_view);
//
//        //set recycler view properties
//        mRecyclerView.setHasFixedSize(true);
//        layoutManager = new LinearLayoutManager(this);
//        mRecyclerView.setLayoutManager(layoutManager);

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

    }

    private void startMyActivity(Class c){
        Intent intent = new Intent(this,c);
        startActivity(intent);
    }
    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}