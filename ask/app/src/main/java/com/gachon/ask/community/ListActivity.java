package com.gachon.ask.community;

import static com.gachon.ask.util.Util.RC_SIGN_IN;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gachon.ask.CommunityFragment;
import com.gachon.ask.CustomAdapter;
import com.gachon.ask.R;
import com.gachon.ask.WriteInfo;
import com.gachon.ask.WritingActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class ListActivity extends AppCompatActivity {
    private FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
    public static Context mContext;

    List<WriteInfo> writeInfoList = new ArrayList<>();
    RecyclerView mRecyclerView;
    //layout manager for recyclerview
    RecyclerView.LayoutManager layoutManager;
//    Button btnAddPost;
    FloatingActionButton btnAddPost;
    //firestore instance
    FirebaseFirestore db;
    CustomAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_community_list);
        mContext = this;

        btnAddPost = findViewById(R.id.btn_add_post);
        btnAddPost.setOnClickListener(view -> {
//            Intent intent = new Intent(getApplicationContext(), WritingActivity.class);
//            startActivity(intent);
            startMyActivity(WritingActivity.class);
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startMyActivity(CommunityFragment.class);
    }



    private void showData(int flag, String[] result) {
        db.collection("Posts")
                .orderBy("createdAt", Query.Direction.DESCENDING) // show from the recent posts
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        //show data
                        for (DocumentSnapshot doc : task.getResult()) {

                            WriteInfo writeInfo = new WriteInfo(
                                    doc.getString("posts_id"),
                                    doc.getString("nickname"),
                                    doc.getString("title"),
                                    doc.getString("contents"),
                                    doc.getString("publisher"),
                                    doc.getString("category"),
                                    doc.getTimestamp("createdAt"));


                        }

                        //adapter
                        adapter = new CustomAdapter(ListActivity.this, writeInfoList);
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
        setContentView(R.layout.fragment_community_list);
        mContext = this;


        //init firestore
        db = FirebaseFirestore.getInstance();

        writeInfoList.clear();
        //initialize views
        mRecyclerView = findViewById(R.id.recycler_view);

        //set recycler view properties
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        Intent intent = getIntent();



    }

    private void startMyActivity(Class c){
        Intent intent = new Intent(this,c);
        startActivity(intent);
    }
    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}