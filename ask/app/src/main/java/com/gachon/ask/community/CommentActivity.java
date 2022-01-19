package com.gachon.ask.community;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gachon.ask.WritingActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.gachon.ask.R;

public class CommentActivity extends AppCompatActivity {
    private static final String TAG = "CommentActivity";
    private String post_id;
    String comment;
    String nickname;
    String profile_image;
    String time;
    Timestamp timestamp;
    long num_comment;
    EditText et_comment;
    Button btn_submit;
    FirebaseFirestore db;
    private FirebaseUser user;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    CommentAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        user = FirebaseAuth.getInstance().getCurrentUser();
        et_comment = (EditText) findViewById(R.id.et_comment);
        btn_submit = (Button) findViewById(R.id.btn_submit);

        Intent intent = getIntent();
        post_id = intent.getStringExtra("post_id");
        db = FirebaseFirestore.getInstance();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(CommentActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CommentAdapter();


    }

    @Override
    public void onResume() {
        super.onResume();
        setContentView(R.layout.activity_comment);


        setContentView(R.layout.activity_comment);
        user = FirebaseAuth.getInstance().getCurrentUser();


        Intent intent = getIntent();
        post_id = intent.getStringExtra("post_id");
        db = FirebaseFirestore.getInstance();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(CommentActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CommentAdapter();
        showData();


        et_comment = (EditText) findViewById(R.id.et_comment);
        btn_submit = (Button) findViewById(R.id.btn_submit);

        /* 전송 버튼 클릭 */
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment();
                et_comment.setText(""); //빈칸으로 초기화
            }
        });


    }


    private void showData() {
        /* 댓글 가져오기 */
        db.collection("Comment")
                .orderBy("time", Query.Direction.ASCENDING)
                .whereEqualTo("post_id", post_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                profile_image = document.get("profile_image").toString();
                                nickname = document.get("nickname").toString();
                                comment = document.get("content").toString();
                                time = getTime((Timestamp) document.get("time"));

//                                adapter.addItem(new CommentInfo(comment, nickname, post_id, profile_image, time));
                                adapter.addItem(new CommentInfo(comment, nickname, post_id, time));
                                recyclerView.setAdapter(adapter);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        /* 포스트 댓글 수 가져오기 */
        db.collection("Posts")
                .whereEqualTo("post_id", post_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                updateNumComment();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /* 댓글 등록하기 */
    private void addComment() {
        comment = et_comment.getText().toString();
        String uid = user.getUid(); // 댓글 쓴 유저의 uid
        timestamp = new Timestamp(new Date()); // 댓글 등록한 시간
        time = getTime(timestamp);


        db.collection("user")
                .whereEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                nickname = document.get("userNickName").toString();
//                                        profile_image = document.get("profileImage").toString();
                                //Comment DB에 데이터 추가
                                Map<String, Object> data = new HashMap<>();
                                data.put("content", comment);
                                data.put("nickname", nickname);
                                data.put("post_id", post_id);
//                                        data.put("profile_image", profile_image);
                                data.put("time", timestamp);

                                db.collection("Comment")
                                        .add(data)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                num_comment++;
                                                updateNumComment();
//                                                num_comment = (long) document.get("num_comment");
                                                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error adding document", e);
                                            }
                                        });


                                //어댑터에 값 전달
//                                        adapter.addItem(new CommentInfo(comment, nickname, post_id, profile_image, time));
                                adapter.addItem(new CommentInfo(comment, nickname, post_id, time));
                                recyclerView.setAdapter(adapter);

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    static String getTime(Timestamp time) {
        Date date_createdAt = time.toDate();//Date형식으로 변경
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm");
        String txt_createdAt = formatter.format(date_createdAt);
        return txt_createdAt;
    }

    public void updateNumComment() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference commentRef = db.collection("Posts").document(post_id);
        commentRef
                .update("num_comment", num_comment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

}
