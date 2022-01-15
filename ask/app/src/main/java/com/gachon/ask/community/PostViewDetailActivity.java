package com.gachon.ask.community;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.gachon.ask.R;


// show the post right after the writing
public class PostViewDetailActivity extends AppCompatActivity {
    private static final String TAG = "PostViewDetailActivity";
    private FirebaseUser user;
    private String posts_id;
    int int_num_heart;
    int int_num_comment;
    Boolean heart_clicked = false;
    String sender_uid;
    ArrayList userlist_heart;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postview_detail);

        // get posts_id from WritingActivity
        Intent intent = getIntent();
        posts_id = intent.getStringExtra("posts_id");
        // add filed posts_id
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference db_post = db.collection("Posts").document(posts_id);
        db_post
                .update("posts_id", posts_id)// change the posts_id value from the WritingActivity
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

        ImageView iv_heart = findViewById(R.id.ic_heart);

        TextView nickname = (TextView) findViewById(R.id.tv_nickname);
        TextView created_at = (TextView) findViewById(R.id.tv_created_At);
        TextView contents = (TextView) findViewById(R.id.tv_contents);
        TextView heart = (TextView) findViewById(R.id.tv_heart);
        TextView comment = (TextView) findViewById(R.id.comment_count);



        /* Show post data */

        DocumentReference docRef = db.collection("Posts").document(posts_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        user = FirebaseAuth.getInstance().getCurrentUser();
                        sender_uid = user.getUid(); // 알림을 보내는 사람의 uid

                        String txt_category = document.getData().get("category").toString();
                        String txt_nickname = document.getData().get("nickname").toString();
                        String txt_contents = document.getData().get("contents").toString();
                        Timestamp timestamp_createdAt = (Timestamp) document.getData().get("createdAt"); // get the timestamp
                        Date date_createdAt = timestamp_createdAt.toDate();// change timestamp as date format
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy년 MM월 HH시 mm분 ss초");
                        String txt_createdAt = formatter.format(date_createdAt);
                        int_num_heart = Integer.parseInt(String.valueOf(document.getData().get("num_heart")));
                        int_num_comment = Integer.parseInt(String.valueOf(document.getData().get("num_comment")));
                        userlist_heart = (ArrayList<String>) document.get("userlist_heart");
                        int_num_heart = userlist_heart.size();

                        nickname.setText(txt_nickname);
                        created_at.setText(txt_createdAt);
                        contents.setText(txt_contents);
                        heart.setText(String.valueOf(int_num_heart));
                        comment.setText(String.valueOf(int_num_comment));


                        System.out.println("userlist_heart.size(): "+userlist_heart.size());
                        System.out.println("userlist_heart.isEmpty(): "+userlist_heart.isEmpty());
                        System.out.println("userlist_heart: "+userlist_heart);



                        if(userlist_heart.isEmpty()){// 하트 리스트가 비어있다면(디폴트)
                            iv_heart.setImageResource(R.drawable.baseline_favorite_border_24);
                            heart_clicked=false;
                        }
                        else if(userlist_heart.contains(user.getUid())){//현재 유저가 해당 글에 이미 하트를 눌렀다면
                            iv_heart.setImageResource(R.drawable.baseline_favorite_24); //하트 채워두기
                            heart_clicked=true;
                        }

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }

        });


        /* Heart click event */

        iv_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                heart_clicked = !(heart_clicked);
                if (heart_clicked) {
                    int_num_heart++;//하트 수 올리고
                    //posts에 좋아요 누른사람 arraylist 만들어서 uid 넣기
                    userlist_heart.add(sender_uid);
                    docRef.update("userlist_heart", FieldValue.arrayUnion(sender_uid)); //파이어스토어에 추가
                    iv_heart.setImageResource(R.drawable.baseline_favorite_24);

                } else {
                    int_num_heart--;//하트 수 내리고
                    userlist_heart.remove(sender_uid);
                    docRef.update("userlist_heart", FieldValue.arrayRemove(sender_uid)); //파이어스토어에서 삭제
                    iv_heart.setImageResource(R.drawable.baseline_favorite_border_24);
                }
                heart.setText(String.valueOf(int_num_heart));// 텍스트 보여주기
                DocumentReference docRef = db.collection("Posts").document(posts_id);
                docRef
                        .update("num_heart", int_num_heart)
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
        });

    }

    private void toastMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
