package com.gachon.ask;


import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gachon.ask.community.CommunityCategoryActivity;
import com.gachon.ask.community.PostViewActivity;
import com.gachon.ask.LevelSystem;
import com.gachon.ask.util.model.User;
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

import java.util.ArrayList;
import java.util.Date;

public class WritingActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user;
    private String post_id;
    private String nickname;
    private String category;
    private int num_heart;
    private int num_comment;
    private ArrayList userlist_heart = new ArrayList();
    Button btn_upload;
    ImageButton btn_cancel;
    Intent intent;
    Handler handler = new Handler();
    User level_user;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);

        intent = getIntent();
        category = intent.getStringExtra("category");
        TextView textView = findViewById(R.id.tv_category);
        textView.setText(category);

        // upload button
        btn_upload = findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postCheck();
            }
        });
        // cancel button
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    // check before upload the post
    private void postCheck() {

        String contents = ((EditText) findViewById(R.id.et_contents)).getText().toString();
        Timestamp created_at = new Timestamp(new Date());

        if (contents.length() > 0) {
            user = FirebaseAuth.getInstance().getCurrentUser();
            String publisher = user.getUid();
            String tempPostId = "tempID"; // update post id later


            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseUser users = FirebaseAuth.getInstance().getCurrentUser();
            DocumentReference docRef = db.collection("user").document(users.getUid());

            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        level_user = task.getResult().toObject(User.class);
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            nickname = document.getData().get("userNickName").toString();
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());


                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                }
            });


            //delay to getting user nickname from firestore
            handler.postDelayed(new Runnable() {
                public void run() {
                    startToast("업로드 중입니다...");
                    WriteInfo writeInfo = new WriteInfo(tempPostId, nickname, contents, publisher,
                            category, created_at, num_heart, num_comment, userlist_heart);
                    postUploader(writeInfo);
                }
            }, 1000); // 1sec


        } else {
            startToast("내용을 입력해주세요.");
        }

    }

    // upload the post
    private void postUploader(WriteInfo writeInfo) {

        LevelSystem lvlSystem = new LevelSystem();
        db.collection("Posts").add(writeInfo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        post_id = documentReference.getId();
                        updatePostId(post_id);
                        startToast("등록되었습니다!");

                        lvlSystem.addExp(level_user, 30); //경험치 30 추가 확인
                        startToast("30 경험치를 획득하였습니다!");

                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        startToast("등록되지 않았습니다.");
                    }
                });

    }


    private void updatePostId(String post_id){
        DocumentReference docRef = db.collection("Posts").document(post_id);
        docRef
                .update("post_id", post_id)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + post_id);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating post id", e);
                    }
                });

    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}