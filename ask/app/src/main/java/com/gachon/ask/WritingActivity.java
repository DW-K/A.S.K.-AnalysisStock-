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

import com.gachon.ask.community.PostViewActivity;
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
    public static final Integer UPLOAD_POST = 110;
    private FirebaseUser user;
    private String posts_id;
    private String nickname;
    private String category;
    Button btn_upload;
    ImageButton btn_cancel;
    Intent intent;

    Handler handler = new Handler();


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


            nickname = "익명"; // 임시 닉네임
            ArrayList participants = new ArrayList();
            String publisher = user.getUid();
            participants.add(publisher); //add writer(host)'s uid to the arraylist participants
            String postId = "tempID";

            // get user nickname from the Users
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseUser users = FirebaseAuth.getInstance().getCurrentUser();
            DocumentReference docRef = db.collection("Users").document(users.getUid());

            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            nickname = document.getData().get("nickname").toString();


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
                    WriteInfo writeInfo = new WriteInfo(postId, nickname, contents, publisher,
                            category, created_at);
                    postUploader(writeInfo);
                }
            }, 1000); // 1sec


        } else {
            startToast("내용을 입력해주세요.");
        }

    }

    // upload the post
    private void postUploader(WriteInfo writeInfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        db.collection("Posts").add(writeInfo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        posts_id = documentReference.getId();
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        startToast("등록되었습니다!");


                        // show the post right after the writing
                        Intent intent = new Intent(getApplicationContext(), PostViewActivity.class);
                        intent.putExtra("posts_id", posts_id); // send posts_id
                        startActivityForResult(intent, UPLOAD_POST);

                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        startToast("등록에 실패하였습니다.");
                    }
                });

    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}