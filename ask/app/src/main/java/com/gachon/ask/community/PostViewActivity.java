package com.gachon.ask.community;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gachon.ask.WriteInfo;
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
import java.util.HashMap;
import java.util.Map;

import com.gachon.ask.R;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;


// show the post right after the writing
public class PostViewActivity extends AppCompatActivity {
    private static final String TAG = "PostViewActivity";
    private FirebaseUser user;
    private String post_id, publisher;
    FirebaseFirestore db;
    ImageView iv_heart, iv_comment;
    String nickname, comment, time;
    int int_num_heart;
    int int_num_comment;
    Boolean heart_clicked = false;
    String sender_uid;
    EditText et_comment;
    Button btn_submit;
    ArrayList userlist_heart;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    CommentAdapter adapter;
    DocumentReference PostRef;
    TextView post_nickname, post_created_at, post_contents, num_heart, num_comment;
    Handler handler = new Handler();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postview);

        // get post_id from CommunityCategoryActivity
        Intent intent = getIntent();
        post_id = intent.getStringExtra("post_id");
        FirebaseFirestore db = FirebaseFirestore.getInstance();





    }

    @Override
    public void onResume() {
        super.onResume();
        setContentView(R.layout.activity_postview);
        user = FirebaseAuth.getInstance().getCurrentUser();

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
//        post_id = intent.getStringExtra("post_id");
        PostRef = db.collection("Posts").document(post_id);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(PostViewActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        post_nickname = (TextView) findViewById(R.id.tv_post_nickname);
        post_created_at = (TextView) findViewById(R.id.tv_post_created_At);
        post_contents = (TextView) findViewById(R.id.tv_post_contents);
        num_heart = (TextView) findViewById(R.id.tv_post_heart);
        num_comment = (TextView) findViewById(R.id.tv_post_comment);

        iv_heart = findViewById(R.id.ic_heart);
        iv_comment = findViewById(R.id.ic_comment);



        /* post 보여주기 */
        showPost();
        /* comment 보여주기 */
        showComment();

        et_comment = (EditText) findViewById(R.id.et_comment);
        btn_submit = (Button) findViewById(R.id.btn_submit);

        /* 댓글 전송 버튼 클릭 */
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!et_comment.getText().toString().replace(" ", "").equals("")) {
                    addComment();
                    et_comment.setText(""); //빈칸으로 초기화
                    updateNumComment(); // 게시글에 댓글 개수 update

                    startToast("작성되었습니다.");
                    //delay to getting user nickname from firestore
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            showPost();
                            showComment(); // 초기화도 함께 할거라고 생각함

                        }
                    }, 1000); // 1sec

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
                    PostRef.update("userlist_heart", FieldValue.arrayUnion(sender_uid)); //파이어스토어에 추가
                    iv_heart.setImageResource(R.drawable.baseline_favorite_24);

                } else {
                    int_num_heart--;//하트 수 내리고
                    userlist_heart.remove(sender_uid);
                    PostRef.update("userlist_heart", FieldValue.arrayRemove(sender_uid)); //파이어스토어에서 삭제
                    iv_heart.setImageResource(R.drawable.baseline_favorite_border_24);
                }
                updateNumHeart();
                num_heart.setText(String.valueOf(int_num_heart));// 텍스트 보여주기

            }
        });

    }

    /* post 보여주기 */

    private void showPost() {
        System.out.println("post_id: "+post_id);
        PostRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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

                        post_nickname.setText(txt_nickname);
                        post_created_at.setText(txt_createdAt);
                        post_contents.setText(txt_contents);
                        num_heart.setText(String.valueOf(int_num_heart));
                        num_comment.setText(String.valueOf(int_num_comment));


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


    }

    private void showComment(){
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager = new LinearLayoutManager(PostViewActivity.this, LinearLayoutManager.VERTICAL, false);
        adapter = new CommentAdapter();

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
                                publisher = document.get("publisher").toString();
                                comment = document.get("content").toString();
                                time = getTime((Timestamp) document.get("time"));

//                                adapter.addItem(new CommentInfo(comment, nickname, post_id, profile_image, time));
                                adapter.addItem(new CommentInfo(comment, nickname, post_id, publisher, time));
                                recyclerView.setAdapter(adapter);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    /* 댓글 등록하기 */
    private void addComment() {
        String comment = et_comment.getText().toString();
        String uid = user.getUid(); // 댓글 쓴 유저의 uid(현재 유저)
        Timestamp timestamp = new Timestamp(new Date()); // 댓글 등록한 시간
        String time = getTime(timestamp);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager = new LinearLayoutManager(PostViewActivity.this, LinearLayoutManager.VERTICAL, false);
        adapter = new CommentAdapter();


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("user")
                .whereEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String nickname = document.get("userNickName").toString();
                                publisher = uid;
//                                        profile_image = document.get("profileImage").toString();
                                //Comment DB에 데이터 추가
                                Map<String, Object> data = new HashMap<>();
                                data.put("content", comment);
                                data.put("nickname", nickname);
                                data.put("publisher", publisher);
                                data.put("post_id", post_id);
//                                        data.put("profile_image", profile_image);
                                data.put("time", timestamp);

                                db.collection("Comment")
                                        .add(data)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                int_num_comment++;
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
                                adapter.addItem(new CommentInfo(comment, nickname, post_id, publisher, time));
                                recyclerView.setAdapter(adapter);

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void updateNumHeart() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference commentRef = db.collection("Posts").document(post_id);
        commentRef
                .update("num_heart", int_num_heart)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "updateNumHeart successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating updateNumHeart", e);
                    }
                });
    }
    public void updateNumComment() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference commentRef = db.collection("Posts").document(post_id);
        commentRef
                .update("num_comment", int_num_comment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "updateNumComment successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating updateNumComment", e);
                    }
                });
    }
    static String getTime(Timestamp time) {
        Date date_createdAt = time.toDate();//Date형식으로 변경
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd HH:mm");
        String txt_createdAt = formatter.format(date_createdAt);
        return txt_createdAt;
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
