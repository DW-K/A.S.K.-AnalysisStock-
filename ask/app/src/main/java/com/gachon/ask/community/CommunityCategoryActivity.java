package com.gachon.ask.community;

import android.annotation.SuppressLint;
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

import com.gachon.ask.MyPageFragment;
import com.gachon.ask.R;
import com.gachon.ask.WritingActivity;
import com.gachon.ask.util.Firestore;
import com.gachon.ask.util.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class CommunityCategoryActivity extends AppCompatActivity {
    final String TAG = "CC Activity";
    private User user;
    private String profileImgURL = null;
    public static Context mContext;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<PostInfo> postInfoList = new ArrayList<>();
    //FirebaseUser user;
    RecyclerView mRecyclerView;
    //layout manager for recyclerview
    RecyclerView.LayoutManager layoutManager;
    String selected_category;
    TextView categoryName;
    Button btnAddPost;
    TextView tv_no_post;
    CommunityCategoryAdapter adapter;
    int postCount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_list);
        mContext = this;
        Intent intent = getIntent();
        selected_category = intent.getStringExtra("selected_category");
        categoryName = findViewById(R.id.category_name);
        categoryName.setText(selected_category);

        //init firestore
        db = FirebaseFirestore.getInstance();
        adapter = new CommunityCategoryAdapter();

        tv_no_post = findViewById(R.id.tv_no_post);
        postCount = 0;

        btnAddPost = findViewById(R.id.btn_add_post);
        btnAddPost.setOnClickListener(view -> {
            Intent addPostIntent = new Intent(getApplicationContext(), WritingActivity.class);
            addPostIntent.putExtra("selected_category", selected_category);
            startActivity(addPostIntent);
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    private void showData() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Posts")
                .orderBy("createdAt", Query.Direction.DESCENDING) // show from the recent posts
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //show data
                            for (DocumentSnapshot doc : task.getResult()) {

                                try {
                                    String post_id = doc.getData().get("post_id").toString();
                                    String nickname = doc.getData().get("nickname").toString();
                                    String contents = doc.getData().get("contents").toString();
                                    String publisher = doc.getData().get("publisher").toString();
                                    String category = doc.getData().get("category").toString();
                                    Timestamp createdAt = (Timestamp) doc.getData().get("createdAt");
                                    Integer num_heart = Integer.parseInt(String.valueOf(doc.getData().get("num_heart")));
                                    Integer num_comment = Integer.parseInt(String.valueOf(doc.getData().get("num_comment")));

                                    /* ????????? ????????? ?????? ?????? */
                                    // 1???. ????????? pulisher(???????????? ?????? ????????? uid)value ??? ????????? ?????? ???????????? userProfileImgURL ?????? ???????????????.
                                    // 2???. ????????? ????????? URL?????? adapter????????? ???????????? ??????
                                    // ??? ???, PostInfo class????????? url ?????? ????????? getter,setter ???????????? ????????? ???.

                                    // 1???
                                    Firestore.getUserData(publisher).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()){
                                                Log.d(TAG, "Entry task successful!!!");
                                                user = task.getResult().toObject(User.class);

                                                if(user.getUserProfileImgURL() != null) {
                                                    profileImgURL = user.getUserProfileImgURL();
                                                    Log.d(TAG, "Profile Image value1 : "+profileImgURL);
                                                    if(category.equals(selected_category)) {
                                                        // 2???
                                                        adapter.addItem(new PostInfo(post_id, nickname, contents, publisher, category, profileImgURL, createdAt, num_heart, num_comment));
                                                        Log.d(TAG, "Profile Image value2 : "+ profileImgURL);
                                                        mRecyclerView.setAdapter(adapter);
                                                        postCount = adapter.getItemCount();

                                                        tv_no_post = findViewById(R.id.tv_no_post);
                                                        System.out.println("postCount:"+postCount);
                                                        if(postCount==0 && postCount>0){
                                                            tv_no_post.setVisibility(View.VISIBLE);
                                                        }else tv_no_post.setVisibility(View.INVISIBLE);


//                                                        System.out.println("itemcount:"+postCount);
                                                    }
                                                }else {
                                                    Log.d(TAG, "Profile Image NULL");
                                                }
                                            }else{
                                                Log.d(TAG,"Error from getUserData");
                                            }
                                        }
                                    });
                                } catch (RuntimeException e) {
                                    System.out.println(e);

                                }
                            }
                            Log.v("TAG", String.valueOf(postInfoList.size()));
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //called when there is any error while retrieving
                        startToast(e.getMessage());
                    }
                });
        System.out.println("adapter.getItemCount():"+adapter.getItemCount());

    }


    @Override
    public void onResume() {
        super.onResume();
        setContentView(R.layout.activity_community_list);


        Intent intent = getIntent();
        selected_category = intent.getStringExtra("selected_category");
        categoryName = findViewById(R.id.category_name);
        categoryName.setText(selected_category);


        btnAddPost = findViewById(R.id.btn_add_post);
        btnAddPost.setOnClickListener(view -> {
            Intent addPostIntent = new Intent(getApplicationContext(), WritingActivity.class);
            addPostIntent.putExtra("category", selected_category);
            startActivity(addPostIntent);
        });

        //init firestore
        db = FirebaseFirestore.getInstance();
        //initialize views
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);

        //set recycler view properties
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
//        adapter.notifyDataSetChanged();
        adapter = new CommunityCategoryAdapter();
        showData();



    }


    static String getTime(Timestamp time) {
        Date date_createdAt = time.toDate();//Date???????????? ??????
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd HH:mm");
        String txt_createdAt = formatter.format(date_createdAt).toString();
        return txt_createdAt;
    }


    private void startMyActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}